package io.github.wanoliveiraa.mscliente.Controller;

import io.github.wanoliveiraa.mscliente.Application.ClienteService;
import io.github.wanoliveiraa.mscliente.Model.Cliente;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Object> criarCliente(@RequestBody Cliente cliente) {
        try{
            Cliente clienteSalvo = clienteService.saveCliente(cliente);
            return ResponseEntity.status(HttpStatus.OK).body(clienteSalvo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao tentar salvar o cliente.");
        }
    }
    @PutMapping
    public ResponseEntity<Object> atualizarCliente(@RequestBody Cliente cliente) {
        try{
            Optional<Cliente> clienteSalvo = clienteService.atualizarCliente(cliente);
            return ResponseEntity.status(HttpStatus.OK).body(clienteSalvo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao tentar atualizar o cliente.");
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> listaClientePorId(@PathVariable Long id) {
        try{
            Optional<Cliente> cliente = clienteService.listaClientePorId(id);
            return ResponseEntity.status(HttpStatus.OK).body(cliente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao tentar atualizar o cliente.");
        }
    }

    @GetMapping()
    public ResponseEntity<Object> listaClienteTodos() {
        try{
            List<Cliente> cliente = clienteService.listarTodosClientes();
            return ResponseEntity.status(HttpStatus.OK).body(cliente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao tentar atualizar o cliente.");
        }
    }

}
