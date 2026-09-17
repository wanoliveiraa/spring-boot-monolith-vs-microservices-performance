package io.github.wanoliveiraa.mspedido.Application;

import io.github.wanoliveiraa.mspedido.Infra.ClienteResourseClient;
import io.github.wanoliveiraa.mspedido.Infra.PedidoRepository;
import io.github.wanoliveiraa.mspedido.Infra.ProdutoResourceClient;
import io.github.wanoliveiraa.mspedido.Model.DadosCliente;
import io.github.wanoliveiraa.mspedido.Model.ItemPedido;
import io.github.wanoliveiraa.mspedido.Model.Pedido;
import io.github.wanoliveiraa.mspedido.Model.DadosProduto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    private final ClienteResourseClient clienteResourseClient;

    private final ProdutoResourceClient produtoResourceClient;

    public Pedido criarPedido(Long clienteId, List<ItemPedido> itens) {
        // Verifica se o cliente existe
        ResponseEntity<DadosCliente> clienteResponse = clienteResourseClient.listaClientePorId(clienteId);
        if (!clienteResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Cliente não encontrado");
        } else {
            clienteResponse.getBody();
        }

        Pedido pedido = new Pedido();
        pedido.setClienteId(clienteId);
        pedido.setData(LocalDate.now());

        List<ItemPedido> itensAtualizados = new ArrayList<>();

        for (ItemPedido item : itens) {
            Long produtoId = item.getProdutoId();
            ResponseEntity<DadosProduto> produtoResponse = produtoResourceClient.listarProdutoPorId(produtoId);

            if (!produtoResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Produto não encontrado: " + produtoId);
            } else {
                produtoResponse.getBody();
            }

            item.setPedido(pedido);
            item.setProdutoId(produtoId); // importante garantir isso
            item.setProduto(produtoResponse.getBody()); // preencher o @Transient
            itensAtualizados.add(item);
        }

        pedido.setItens(itensAtualizados);
        pedido.setCliente(clienteResponse.getBody()); // preencher o @Transient

        return pedidoRepository.save(pedido);
    }

    public Optional<Pedido> atualizarPedidos(Pedido pedidoAtualizados) {
        return pedidoRepository.findById(pedidoAtualizados.getId()).map(pedido -> {
            pedido.setData(pedidoAtualizados.getData());
            return pedidoRepository.save(pedido);
        });
    }

    public Optional<Pedido> listaPedidoPorId(Long id) {
        Optional<Pedido> pedidoOptional = pedidoRepository.findById(id);

        pedidoOptional.ifPresent(pedido -> {
            // Buscar dados do cliente via Feign
            DadosCliente cliente = clienteResourseClient.listaClientePorId(pedido.getClienteId()).getBody();
            pedido.setCliente(cliente);

            // Buscar dados dos produtos via Feign
            for (ItemPedido item : pedido.getItens()) {
                if (item.getProdutoId() != null) {
                    DadosProduto produto = produtoResourceClient.listarProdutoPorId(item.getProdutoId()).getBody();
                    item.setProdutoId(produto.getId());
                    item.setProduto(produto);
                    item.setPedido(pedido);
                }
            }
        });

        return pedidoOptional;
    }


    public List<Pedido> listarTodosPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();

        for (Pedido pedido : pedidos) {
            DadosCliente cliente = clienteResourseClient.listaClientePorId(pedido.getClienteId()).getBody();
            pedido.setCliente(cliente);

            for (ItemPedido item : pedido.getItens()) {
                Long produtoId = item.getProdutoId();
                if (produtoId != null) {
                    ResponseEntity<DadosProduto> produto = produtoResourceClient.listarProdutoPorId(produtoId);
                    if (!produto.getStatusCode().is2xxSuccessful() || produto.getBody() == null) {
                        throw new RuntimeException("Produto não encontrado: " + produtoId);
                    }
                    item.setProduto(produto.getBody());
                } else {
                    // Aqui pode logar que produtoId está nulo para investigar
                    System.out.println("produtoId está nulo no item " + item.getId());
                }
            }
        }

        return pedidos;
    }

    public List<Pedido> listarPedidosDoClienteComDetalhes(Long clienteId) {
        // 1. Buscar pedidos filtrando pelo clienteId
        List<Pedido> pedidos = pedidoRepository.findPedidosByClienteId(clienteId);

        // 2. Buscar dados completos do cliente via Feign
        DadosCliente dadosCliente = clienteResourseClient.listaClientePorId(clienteId).getBody();

        for (Pedido pedido : pedidos) {
            // 3. Setar dados do cliente no pedido (campo @Transient)
            pedido.setCliente(dadosCliente);

            // 4. Para cada item, buscar dados do produto e setar no campo @Transient
            for (ItemPedido item : pedido.getItens()) {
                DadosProduto dadosProduto = produtoResourceClient.listarProdutoPorId(item.getProdutoId()).getBody();
                item.setProduto(dadosProduto);
            }
        }

        return pedidos;
    }


    public List<Pedido> buscarPorClienteSimples(Long clienteId) {
        return pedidoRepository.findPedidosByClienteId(clienteId);
    }
}
