package edu.pe.vallegrande.CopilotAI.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.pe.vallegrande.CopilotAI.model.Copilot;
import edu.pe.vallegrande.CopilotAI.repository.CopilotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CopilotService {

    private final CopilotRepository repository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<Copilot> crearConsulta(String pregunta) {
        if (pregunta == null || pregunta.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("La pregunta no puede estar vacía."));
        }

        return obtenerRespuestaAPI(pregunta.trim())
                .flatMap(respuesta -> {
                    Copilot c = new Copilot(null, pregunta.trim(), respuesta, LocalDate.now(), 'A');
                    return repository.save(c);
                });
    }

    public Mono<String> obtenerRespuestaAPI(String pregunta) {
        String body;
        try {
            body = String.format("""
            {
                "message": %s,
                "conversation_id": null,
                "markdown": true
            }
            """, objectMapper.writeValueAsString(pregunta)); // Escapa bien el string
        } catch (Exception e) {
            return Mono.error(e);
        }

       HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://copilot5.p.rapidapi.com/copilot"))
                .header("x-rapidapi-key", "41edba6ef7mshedaf8fb7c7e9442p1fc368jsn1274f29ade93")
                .header("x-rapidapi-host", "copilot5.p.rapidapi.com")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return Mono.fromCallable(() -> {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            
            // Convertir la respuesta JSON en un Map para extraer el mensaje
            Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            return (String) data.get("message");  // Extraer solo el mensaje
        });
    }

    public Mono<Copilot> editarConsulta(Long id, String nuevaPregunta) {
        if (nuevaPregunta == null || nuevaPregunta.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("La nueva pregunta no puede estar vacía."));
        }

        return obtenerRespuestaAPI(nuevaPregunta.trim())
                .flatMap(nuevaRespuesta -> 
                        repository.findById(id)
                                .flatMap(c -> {
                                    c.setPregunta(nuevaPregunta.trim());
                                    c.setRespuesta(nuevaRespuesta);
                                    return repository.save(c);
                                })
                );
    }

    public Mono<Void> eliminar(Long id) {
        return repository.deleteById(id);
    }

    public Mono<Copilot> eliminate(Long id) {
        return repository.findById(id)
                .flatMap(c -> {
                    c.setState('I');
                    return repository.save(c);
                });
    }

    public Mono<Copilot> restaurar(Long id) {
        return repository.findById(id)
                .flatMap(c -> {
                    c.setState('A');
                    return repository.save(c);
                });
    }

    public Flux<Copilot> listar() {
        return repository.findAll();
    }

    public Flux<Copilot> listarA() {
        return repository.findByState('A');
    }

    public Flux<Copilot> listarI() {
        return repository.findByState('I');
    }

}
