package edu.pe.vallegrande.CopilotAI.rest;

import edu.pe.vallegrande.CopilotAI.model.Copilot;
import edu.pe.vallegrande.CopilotAI.service.CopilotService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/copilot")
@RequiredArgsConstructor
public class CopilotRest {

    private final CopilotService service;

    // Crear una nueva consulta
    @PostMapping("/crear")
    public Mono<ResponseEntity<Copilot>> crear(@RequestBody Map<String, String> request) {
        String pregunta = request.get("pregunta");
    return service.crearConsulta(pregunta)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(null)));
}

    // Editar una consulta existente
    @PutMapping("/editar/{id}")
    public Mono<ResponseEntity<Copilot>> editar(@PathVariable Long id, @RequestBody String nuevaPregunta) {
        return service.editarConsulta(id, nuevaPregunta)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(null)));
    }

    // Eliminar físico
    @DeleteMapping("/eliminar/{id}")
    public Mono<ResponseEntity<Void>> eliminar(@PathVariable Long id) {
        return service.eliminar(id)
                .thenReturn(ResponseEntity.noContent().build());
    }

    // Eliminar lógico
    @PutMapping("/eliminate/{id}")
    public Mono<ResponseEntity<Copilot>> eliminate(@PathVariable Long id) {
        return service.eliminate(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Restaurar una consulta eliminada lógicamente
    @PutMapping("/restaurar/{id}")
    public Mono<ResponseEntity<Copilot>> restaurar(@PathVariable Long id) {
        return service.restaurar(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Listar todos (incluye inactivos)
    @GetMapping("/listar")
    public Flux<Copilot> listar() {
        return service.listar();
    }

    // Listar solo activos
    @GetMapping("/listarA")
    public Flux<Copilot> listarActivos() {
        return service.listarA();
    }

    // Listar solo inactivos
    @GetMapping("/listarI")
    public Flux<Copilot> listarInactivos() {
        return service.listarI();
    }

}
