package io.github.wanoliveiraa.msproduto.Application;

import io.github.wanoliveiraa.msproduto.Infra.ProdutoRepository;
import io.github.wanoliveiraa.msproduto.Model.Produto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    public Produto salvarProduto(Produto produto) {
        return produtoRepository.save(produto);
    }
    public Optional<Produto> atualizarProdutos(Produto produtoAtualizados) {
        return produtoRepository.findById(produtoAtualizados.getId()).map(produto -> {
            produto.setNome(produtoAtualizados.getNome());
            produto.setPreco(produtoAtualizados.getPreco());
            return produtoRepository.save(produto);
        });
    }

    public List<Produto> listarTodosProdutos() {
        return produtoRepository.findAll();
    }
    public Optional<Produto> listaProdutoPorId(Long id) {
        return produtoRepository.findById(id);
    }
}
