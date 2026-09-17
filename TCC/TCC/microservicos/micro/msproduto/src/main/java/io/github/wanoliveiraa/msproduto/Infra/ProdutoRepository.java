package io.github.wanoliveiraa.msproduto.Infra;

import io.github.wanoliveiraa.msproduto.Model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
