package io.github.wanoliveiraa.mspedido.application.integration;

import io.github.wanoliveiraa.mspedido.infra.ProdutoCacheService;
import io.github.wanoliveiraa.mspedido.model.DadosProduto;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ProdutoSeguroService {

    private final ProdutoCacheService produtoCacheService;

    @Bulkhead(name = "produto", type = Bulkhead.Type.SEMAPHORE)
    @Retry(name = "produto", fallbackMethod = "fallbackBuscarProduto")
    @TimeLimiter(name = "produto")
    public CompletableFuture<DadosProduto> buscarProduto(Long produtoId) {
        return CompletableFuture.supplyAsync(() -> produtoCacheService.buscarProduto(produtoId));
    }

    // Fallback
    private CompletableFuture<DadosProduto> fallbackBuscarProduto(Long produtoId, Throwable t) {
        DadosProduto fallback = new DadosProduto();
        fallback.setId(produtoId);
        fallback.setNome("Produto fallback");
        return CompletableFuture.completedFuture(fallback);
    }
}
