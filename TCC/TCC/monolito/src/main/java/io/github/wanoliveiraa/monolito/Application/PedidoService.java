package io.github.wanoliveiraa.monolito.Application;

import io.github.wanoliveiraa.monolito.Dto.ItemPedidoRequestDTO;
import io.github.wanoliveiraa.monolito.Dto.PedidoRequestDTO;
import io.github.wanoliveiraa.monolito.Infra.ClienteRepository;
import io.github.wanoliveiraa.monolito.Infra.PedidoRepository;
import io.github.wanoliveiraa.monolito.Infra.ProdutoRepository;
import io.github.wanoliveiraa.monolito.Model.Cliente;
import io.github.wanoliveiraa.monolito.Model.ItemPedido;
import io.github.wanoliveiraa.monolito.Model.Pedido;
import io.github.wanoliveiraa.monolito.Model.Produto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;


    private List<ItemPedido> converterItensParaEntidade(List<ItemPedidoRequestDTO> itensDto) {
        return itensDto.stream().map(dto -> {
            // Criamos a ENTIDADE ItemPedido
            ItemPedido item = new ItemPedido();

            // Criamos um 'Produto' com apenas o ID (Será substituído pelo objeto real na função criarPedido)
            Produto produtoPlaceholder = new Produto();
            produtoPlaceholder.setId(dto.getProdutoId());

            item.setProduto(produtoPlaceholder);
            return item;
        }).toList();
    }
    @Transactional
    public Pedido criarPedido(Long clienteId, List<ItemPedido> itens) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // 2. OTIMIZAÇÃO: Busca TODOS os produtos de uma vez (1 Query)
        List<Long> idsProdutos = itens.stream()
                .map(i -> i.getProduto().getId())
                .toList();

        List<Produto> produtosEncontrados = produtoRepository.findAllById(idsProdutos);

        // OTIMIZAÇÃO DE CPU: Transforma lista em Map para busca O(1)
        Map<Long, Produto> mapaProdutos = produtosEncontrados.stream()
                .collect(Collectors.toMap(Produto::getId, Function.identity()));

        // CORREÇÃO DO BUG: Validação - compara o número de IDs únicos
        long countIdsUnicos = idsProdutos.stream().distinct().count();
        if (mapaProdutos.size() != countIdsUnicos) {
            throw new RuntimeException("Erro: Algum produto não foi encontrado");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setData(LocalDate.now());

        // Associa os produtos em memória
        for (ItemPedido item : itens) {
            // CORREÇÃO DO CÓDIGO: Usa o Map para acesso instantâneo (O(1))
            Produto produto = mapaProdutos.get(item.getProduto().getId());

            item.setProduto(produto);
            item.setPedido(pedido); // Garante a ligação bidirecional
        }

        pedido.setItens(itens);

        // Este é o ponto mais lento do seu TCC (Commit por requisição)
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void criarLotePedidos(List<PedidoRequestDTO> listaPedidosDto) {

        // -------------------------------------------
        // 1. Buscar todos os clientes necessários (1 única vez)
        // -------------------------------------------

        Map<Long, Cliente> mapaClientes = clienteRepository
                .findAllById(
                        listaPedidosDto.stream()
                                .map(PedidoRequestDTO::getClienteId)
                                .distinct()
                                .toList()
                )
                .stream()
                .collect(Collectors.toMap(Cliente::getId, c -> c));


        // -------------------------------------------
        // 2. Coletar TODOS os IDs de produtos do lote (única busca)
        // -------------------------------------------

        List<Long> idsProdutos = listaPedidosDto.stream()
                .flatMap(dto -> dto.getItens().stream())
                .map(ItemPedidoRequestDTO::getProdutoId)
                .distinct()
                .toList();

        Map<Long, Produto> mapaProdutos = produtoRepository.findAllById(idsProdutos)
                .stream()
                .collect(Collectors.toMap(Produto::getId, p -> p));

        if (mapaProdutos.size() != idsProdutos.size()) {
            throw new RuntimeException("Algum produto informado não existe");
        }

        // -------------------------------------------
        // 3. Criar todos os pedidos e itens em memória
        // -------------------------------------------

        List<Pedido> pedidosParaSalvar = new ArrayList<>();

        for (PedidoRequestDTO pedidoDto : listaPedidosDto) {

            Cliente cliente = mapaClientes.get(pedidoDto.getClienteId());
            if (cliente == null) {
                throw new RuntimeException("Cliente não encontrado: " + pedidoDto.getClienteId());
            }

            // Cria o pedido
            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            pedido.setData(LocalDate.now());

            List<ItemPedido> itens = new ArrayList<>();

            for (ItemPedidoRequestDTO itemDto : pedidoDto.getItens()) {

                Produto produto = mapaProdutos.get(itemDto.getProdutoId());

                ItemPedido item = new ItemPedido();
                item.setProduto(produto);
                item.setPedido(pedido);

                itens.add(item);
            }

            pedido.setItens(itens);

            pedidosParaSalvar.add(pedido);
        }

        // -------------------------------------------
        // 4. Salvar TODOS os pedidos e itens de uma vez (1 flush)
        // -------------------------------------------

        pedidoRepository.saveAll(pedidosParaSalvar);

        // Hibernate vai persistir tudo com cascade automaticamente.
    }

    public Optional<Pedido> atualizarPedidos(Pedido pedidoAtualizados) {
        return pedidoRepository.findById(pedidoAtualizados.getId()).map(pedido -> {
            pedido.setData(pedidoAtualizados.getData());
            return pedidoRepository.save(pedido);
        });
    }
    public Optional<Pedido> listaPedidoPorId(Long id) {
        return pedidoRepository.findByIdComItens(id);
    }

    public List<Pedido> listarTodosPedidos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> buscarPorCliente(Long clienteId) {
        return pedidoRepository.findPedidosPorCliente(clienteId);
    }

}
