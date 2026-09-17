package io.github.wanoliveiraa.mspedido.infra;


import io.github.wanoliveiraa.mspedido.model.DadosProduto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "produtos")
public class ProdutoCacheService {

    private final ProdutoResourceClient produtoResourceClient;

    @Cacheable
    public DadosProduto buscarProduto(Long produtoId) {

        try {
            ResponseEntity<DadosProduto> response =
                    produtoResourceClient.listarProdutoPorId(produtoId);

            if (!response.getStatusCode().is2xxSuccessful() || !response.hasBody()) {
                throw new RuntimeException("Produto não encontrado na API: " + produtoId);
            }

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Erro ao comunicar com o MSProduto ao buscar produto " + produtoId, e
            );
        }
    }
}
