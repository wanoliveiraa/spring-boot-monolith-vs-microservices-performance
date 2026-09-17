package io.github.wanoliveiraa.mspedido.application.integration;

import io.github.wanoliveiraa.mspedido.infra.ClienteResourseClient;
import io.github.wanoliveiraa.mspedido.model.DadosCliente;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ClienteSeguroService {

    private final ClienteResourseClient clienteResourseClient;

    @Bulkhead(name = "cliente", type = Bulkhead.Type.SEMAPHORE)
    @Retry(name = "cliente", fallbackMethod = "fallbackBuscarCliente")
    @TimeLimiter(name = "cliente")
    public CompletableFuture<DadosCliente> buscarCliente(Long clienteId) {
        return CompletableFuture.supplyAsync(() -> {
            var response = clienteResourseClient.listaClientePorId(clienteId);
            if (response.getBody() == null) {
                throw new RuntimeException("Cliente não encontrado: " + clienteId);
            }
            return response.getBody();
        });
    }

    // Fallback
    private CompletableFuture<DadosCliente> fallbackBuscarCliente(Long clienteId, Throwable t) {
        DadosCliente fallback = new DadosCliente();
        fallback.setId(clienteId);
        fallback.setNome("Cliente fallback");
        return CompletableFuture.completedFuture(fallback);
    }
}
