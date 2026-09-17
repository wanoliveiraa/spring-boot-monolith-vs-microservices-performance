package io.github.wanoliveiraa.msproduto.Controller;

import io.github.wanoliveiraa.msproduto.Application.ProdutoService;
import io.github.wanoliveiraa.msproduto.Model.Produto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<Object> criarProduto(@RequestBody Produto produto) {
        try{
            Produto produtoSalvo = produtoService.salvarProduto(produto);
            return ResponseEntity.status(HttpStatus.OK).body(produtoSalvo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao tentar salvar o produto.");
        }
    }
    @PutMapping
    public ResponseEntity<Object> atualizarCliente(@RequestBody Produto produto) {
        try{
            Optional<Produto> produtoSalvo = produtoService.atualizarProdutos(produto);
            return ResponseEntity.status(HttpStatus.OK).body(produtoSalvo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao tentar atualizar o cliente.");
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> listarProdutoPorId(@PathVariable Long id) {
        try{
            Optional<Produto> produto = produtoService.listaProdutoPorId(id);
            return ResponseEntity.status(HttpStatus.OK).body(produto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao tentar atualizar o cliente.");
        }
    }

    @GetMapping()
    public ResponseEntity<Object> listarTodosProdutos() {
        try{
            List<Produto> produto = produtoService.listarTodosProdutos();
            return ResponseEntity.status(HttpStatus.OK).body(produto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao tentar atualizar o cliente.");
        }
    }

}
