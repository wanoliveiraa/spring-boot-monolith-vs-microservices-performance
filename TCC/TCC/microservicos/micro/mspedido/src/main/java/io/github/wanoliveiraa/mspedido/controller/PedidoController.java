package io.github.wanoliveiraa.mspedido.controller;

import io.github.wanoliveiraa.mspedido.application.PedidoService;
import io.github.wanoliveiraa.mspedido.dto.PedidoRequestDTO;
import io.github.wanoliveiraa.mspedido.model.Pedido;
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
            Pedido pedidoSalvo = pedidoService.criarPedido(pedido.getClienteId(),pedido.getItens());
            return ResponseEntity.status(HttpStatus.OK).body(pedidoSalvo);
        } catch (Exception e) {
            // Logando o erro para detalhamento
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao tentar salvar o cliente: " + e.getMessage());
        }
    }
    @PutMapping
    public ResponseEntity<Object> atualizarPedido(@RequestBody Pedido pedido) {
        try{
            Optional<Pedido> pedidoSalvo = pedidoService.atualizarPedidos(pedido);
            return ResponseEntity.status(HttpStatus.OK).body(pedidoSalvo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao tentar atualizar o pedido.");
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> listarPedidoPorId(@PathVariable Long id) {
        try{
            Optional<Pedido> pedido = pedidoService.listaPedidoPorId(id);
            return ResponseEntity.status(HttpStatus.OK).body(pedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao buscar um pedido.");
        }
    }
    @GetMapping()
    public ResponseEntity<Object> listarTodosPedidos() {
        try{
            List<Pedido> pedido = pedidoService.listarTodosPedidos();
            return ResponseEntity.status(HttpStatus.OK).body(pedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao buscar o pedido.");
        }
    }

    @GetMapping("cliente/{clienteId}")
    public ResponseEntity<Object> listarPedidosPorCliente(@PathVariable Long clienteId) {
        try{
            List<Pedido> pedido = pedidoService.listarPedidosDoClienteComDetalhes(clienteId);
            return ResponseEntity.status(HttpStatus.OK).body(pedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao buscar o pedido do cliente.");
        }
    }
    @GetMapping(params = "clienteId")
    public ResponseEntity<Object> listarPedidosPorClienteSimplies(@RequestParam ("clienteId")Long clienteId) {
        try{
            List<Pedido> pedido = pedidoService.buscarPorClienteSimples(clienteId);
            return ResponseEntity.status(HttpStatus.OK).body(pedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao tentar atualizar o cliente.");
        }
    }

    @PostMapping("/lote")
    public ResponseEntity<String> criarLote(@RequestBody List<PedidoRequestDTO> dtos) {


        pedidoService.criarLotePedidos(dtos);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                "Lote de " + dtos.size()
        );
    }
}
