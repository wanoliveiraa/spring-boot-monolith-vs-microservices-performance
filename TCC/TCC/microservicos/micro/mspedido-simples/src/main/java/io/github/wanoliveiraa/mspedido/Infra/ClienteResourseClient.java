package io.github.wanoliveiraa.mspedido.Infra;

import io.github.wanoliveiraa.mspedido.Model.DadosCliente;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "mscliente", path = "api/clientes")
public interface ClienteResourseClient {

    @GetMapping("/{id}")
    ResponseEntity<DadosCliente> listaClientePorId(@PathVariable("id") Long id);
}
