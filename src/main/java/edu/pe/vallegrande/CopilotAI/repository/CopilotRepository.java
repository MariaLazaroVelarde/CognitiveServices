package edu.pe.vallegrande.CopilotAI.repository;

import edu.pe.vallegrande.CopilotAI.model.Copilot;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CopilotRepository extends ReactiveCrudRepository<Copilot, Long> {
    Flux<Copilot> findByState(char findByState);
}
