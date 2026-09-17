package io.github.wanoliveiraa.mspedido.Infra;

import io.github.wanoliveiraa.mspedido.Model.DadosProduto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "msproduto", path = "api/produtos")
public interface ProdutoResourceClient {

    @GetMapping("/{id}")
    ResponseEntity<DadosProduto> listarProdutoPorId(@PathVariable("id") Long id);
}
