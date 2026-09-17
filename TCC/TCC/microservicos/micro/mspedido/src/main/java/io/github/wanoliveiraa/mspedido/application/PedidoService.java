package io.github.wanoliveiraa.mspedido.application;

import io.github.wanoliveiraa.mspedido.application.integration.ClienteSeguroService;
import io.github.wanoliveiraa.mspedido.application.integration.ProdutoSeguroService;
import io.github.wanoliveiraa.mspedido.dto.ItemPedidoRequestDTO;
import io.github.wanoliveiraa.mspedido.dto.PedidoRequestDTO;
import io.github.wanoliveiraa.mspedido.infra.PedidoRepository;
import io.github.wanoliveiraa.mspedido.infra.ProdutoCacheService;
import io.github.wanoliveiraa.mspedido.model.DadosCliente;
import io.github.wanoliveiraa.mspedido.model.ItemPedido;
import io.github.wanoliveiraa.mspedido.model.Pedido;
import io.github.wanoliveiraa.mspedido.model.DadosProduto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoCacheService produtoCacheService;
    private final ClienteSeguroService clienteSeguroService;
    private final ProdutoSeguroService produtoSeguroService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Pedido criarPedido(Long clienteId, List<ItemPedido> itens) {
        DadosCliente cliente = clienteSeguroService.buscarCliente(clienteId).join();

        Pedido pedido = new Pedido();
        pedido.setClienteId(clienteId);
        pedido.setCliente(cliente);
        pedido.setData(LocalDate.now());

        List<ItemPedido> itensAtualizados = itens.stream()
                .map(item -> {
                    DadosProduto produto = produtoCacheService.buscarProduto(item.getProdutoId());
                    if (produto == null) {
                        produto = produtoSeguroService.buscarProduto(item.getProdutoId()).join();
                    }

                    item.setPedido(pedido);
                    item.setProdutoId(produto.getId());
                    item.setProduto(produto);
                    return item;
                })
                .toList();

        pedido.setItens(itensAtualizados);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void criarLotePedidos(List<PedidoRequestDTO> lote) {

        Map<Long, CompletableFuture<DadosCliente>> futurosClientes = lote.stream()
                .map(PedidoRequestDTO::getClienteId)
                .distinct()
                .collect(Collectors.toMap(
                        id -> id,
                        clienteSeguroService::buscarCliente
                ));

        Map<Long, DadosCliente> mapaClientes = futurosClientes.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().join()
                ));

        Set<Long> produtosIds = lote.stream()
                .flatMap(dto -> dto.getItens().stream())
                .map(ItemPedidoRequestDTO::getProdutoId)
                .collect(Collectors.toSet());

        ExecutorService executor = Executors.newFixedThreadPool(10);;

        Map<Long, CompletableFuture<DadosProduto>> futurosProdutos = produtosIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> CompletableFuture.supplyAsync(
                                () -> {
                                    DadosProduto produto = produtoCacheService.buscarProduto(id);
                                    if (produto != null) return produto;
                                    return produtoSeguroService.buscarProduto(id).join();
                                },
                                executor
                        )
                ));

        Map<Long, DadosProduto> mapaProdutos = futurosProdutos.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().join()
                ));

        executor.shutdown();

        final int CHUNK_SIZE = 50;
        List<Pedido> pedidosChunk = new ArrayList<>();
        for (int i = 0; i < lote.size(); i++) {
            PedidoRequestDTO dto = lote.get(i);
            DadosCliente cliente = mapaClientes.get(dto.getClienteId());

            Pedido pedido = new Pedido();
            pedido.setClienteId(cliente.getId());
            pedido.setCliente(cliente);
            pedido.setData(LocalDate.now());

            List<ItemPedido> itens = dto.getItens().stream()
                    .map(itemDto -> {
                        DadosProduto prod = mapaProdutos.get(itemDto.getProdutoId());
                        ItemPedido item = new ItemPedido();
                        item.setPedido(pedido);
                        item.setProdutoId(prod.getId());
                        item.setProduto(prod);
                        return item;
                    }).toList();

            pedido.setItens(itens);
            pedidosChunk.add(pedido);

            if (pedidosChunk.size() == CHUNK_SIZE || i == lote.size() - 1) {
                pedidoRepository.saveAll(pedidosChunk);
                entityManager.flush();
                entityManager.clear();
                pedidosChunk.clear();
            }
        }
    }


    @Transactional(readOnly = true)
    public Optional<Pedido> listaPedidoPorId(Long id) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        if (pedidoOpt.isEmpty()) return Optional.empty();

        Pedido pedido = pedidoOpt.get();

        CompletableFuture<DadosCliente> futuroCliente = clienteSeguroService.buscarCliente(pedido.getClienteId());

        List<CompletableFuture<DadosProduto>> futurosProdutos = pedido.getItens().stream()
                .map(item -> CompletableFuture.supplyAsync(() -> {
                    try {
                        DadosProduto produto = produtoCacheService.buscarProduto(item.getProdutoId());
                        if (produto == null) {
                            return produtoSeguroService.buscarProduto(item.getProdutoId()).join();
                        }
                        return produto;
                    } catch (Exception e) {
                        return null;
                    }
                }))
                .toList();

        Collection<CompletableFuture<?>> allFuturesList = new ArrayList<>();
        allFuturesList.add(futuroCliente);
        allFuturesList.addAll(futurosProdutos);

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                allFuturesList.toArray(new CompletableFuture[0])
        );

        try {
            allFutures.join();

            DadosCliente cliente = futuroCliente.handle((c, ex) -> c).join();
            pedido.setCliente(cliente);

            for (int i = 0; i < pedido.getItens().size(); i++) {
                DadosProduto produto = futurosProdutos.get(i).handle((p, ex) -> p).join();
                pedido.getItens().get(i).setProduto(produto);
            }

        } catch (Exception e) {
            return Optional.empty();
        }

        return Optional.of(pedido);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarTodosPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        if (pedidos.isEmpty()) return pedidos;

        Set<Long> clientesIds = pedidos.stream().map(Pedido::getClienteId).collect(Collectors.toSet());
        Set<Long> produtosIds = pedidos.stream().flatMap(p -> p.getItens().stream()).map(ItemPedido::getProdutoId).collect(Collectors.toSet());

        Map<Long, CompletableFuture<DadosCliente>> futurosClientes = clientesIds.stream()
                .collect(Collectors.toMap(id -> id, clienteSeguroService::buscarCliente));

        Map<Long, CompletableFuture<DadosProduto>> futurosProdutos = produtosIds.stream()
                .collect(Collectors.toMap(id -> id, id -> CompletableFuture.supplyAsync(() -> {
                    DadosProduto produto = produtoCacheService.buscarProduto(id);
                    if (produto != null) return produto;
                    return produtoSeguroService.buscarProduto(id).join();
                })));

        Collection<CompletableFuture<?>> todasAsFutures = new ArrayList<>();
        todasAsFutures.addAll(futurosClientes.values());
        todasAsFutures.addAll(futurosProdutos.values());

        CompletableFuture.allOf(
                todasAsFutures.toArray(new CompletableFuture[0])
        ).join();

        Map<Long, DadosCliente> mapaClientes = futurosClientes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().handle((cliente, ex) -> cliente).join()));

        Map<Long, DadosProduto> mapaProdutos = futurosProdutos.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().handle((produto, ex) -> produto).join()));

        for (Pedido pedido : pedidos) {
            DadosCliente finalCliente = mapaClientes.get(pedido.getClienteId());
            pedido.setCliente(finalCliente);
            for (ItemPedido item : pedido.getItens()) {
                item.setProduto(mapaProdutos.get(item.getProdutoId()));
            }
        }

        return pedidos;
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPedidosDoClienteComDetalhes(Long clienteId) {
        List<Pedido> pedidos = pedidoRepository.findPedidosByClienteId(clienteId);
        if (pedidos.isEmpty()) return pedidos;

        CompletableFuture<DadosCliente> futuroCliente = clienteSeguroService.buscarCliente(clienteId);

        Set<Long> produtosIds = pedidos.stream()
                .flatMap(p -> p.getItens().stream())
                .map(ItemPedido::getProdutoId)
                .collect(Collectors.toSet());

        Map<Long, CompletableFuture<DadosProduto>> futurosProdutos = produtosIds.stream()
                .collect(Collectors.toMap(id -> id, id -> CompletableFuture.supplyAsync(() -> {
                    DadosProduto produto = produtoCacheService.buscarProduto(id);
                    if (produto != null) return produto;
                    return produtoSeguroService.buscarProduto(id).join();
                })));

        Collection<CompletableFuture<?>> todasAsFutures = new ArrayList<>();
        todasAsFutures.add(futuroCliente);
        todasAsFutures.addAll(futurosProdutos.values());

        CompletableFuture.allOf(
                todasAsFutures.toArray(new CompletableFuture[0])
        ).join();

        DadosCliente cliente = futuroCliente.handle((c, ex) -> c).join();

        Map<Long, DadosProduto> mapaProdutos = futurosProdutos.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().handle((p, ex) -> p).join()));


        final DadosCliente finalCliente = cliente;
        pedidos.forEach(pedido -> {
            pedido.setCliente(finalCliente);
            pedido.getItens().forEach(item -> item.setProduto(mapaProdutos.get(item.getProdutoId())));
        });

        return pedidos;
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPorClienteSimples(Long clienteId) {
        return pedidoRepository.findPedidosByClienteId(clienteId);
    }

    @Transactional
    public Optional<Pedido> atualizarPedidos(Pedido pedidoAtualizados) {
        return pedidoRepository.findById(pedidoAtualizados.getId()).map(pedido -> {
            pedido.setData(pedidoAtualizados.getData());
            return pedidoRepository.save(pedido);
        });
    }
}