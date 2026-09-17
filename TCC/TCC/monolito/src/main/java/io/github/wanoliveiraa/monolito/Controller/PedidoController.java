package io.github.wanoliveiraa.monolito.Controller;

import io.github.wanoliveiraa.monolito.Application.PedidoService;
import io.github.wanoliveiraa.monolito.Dto.PedidoRequestDTO;
import io.github.wanoliveiraa.monolito.Model.Pedido;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Object> criarPedido(@RequestBody Pedido pedido) {
        try{
            Pedido pedidoSalvo = pedidoService.criarPedido(pedido.getCliente().getId(),pedido.getItens());
            return ResponseEntity.status(HttpStatus.OK).body(pedidoSalvo);
        } catch (Exception e) {
            // Logando o erro para detalhamento
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao tentar salvar o cliente: " + e.getMessage());
        }
    }
    @PostMapping("/lote")
    public ResponseEntity<String> criarLote(@RequestBody List<PedidoRequestDTO> dtos) {


        // Chama o método que segura a transação para todo o lote
        pedidoService.criarLotePedidos(dtos);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                "Lote de " + dtos.size()
        );
    }
    @PutMapping
    public ResponseEntity<Object> atualizarPedido(@RequestBody Pedido pedido) {
        try{
            Optional<Pedido> pedidoSalvo = pedidoService.atualizarPedidos(pedido);
            return ResponseEntity.status(HttpStatus.OK).body(pedidoSalvo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao tentar atualizar o cliente.");
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> listarPedidoPorId(@PathVariable Long id) {
        try{
            Optional<Pedido> pedido = pedidoService.listaPedidoPorId(id);
            return ResponseEntity.status(HttpStatus.OK).body(pedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao tentar atualizar o cliente.");
        }
    }

    @GetMapping()
    public ResponseEntity<Object> listarTodosPedidos() {
        try{
            List<Pedido> pedido = pedidoService.listarTodosPedidos();
            return ResponseEntity.status(HttpStatus.OK).body(pedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao tentar atualizar o cliente.");
        }
    }
    @GetMapping("cliente/{clienteId}")
    public ResponseEntity<Object> listarPedidosPorCliente(@PathVariable Long clienteId) {
        try{
            List<Pedido> pedido = pedidoService.buscarPorCliente(clienteId);
            return ResponseEntity.status(HttpStatus.OK).body(pedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao tentar atualizar o cliente.");
        }
    }

}
