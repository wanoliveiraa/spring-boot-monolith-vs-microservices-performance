package io.github.wanoliveiraa.monolito.Infra;

import io.github.wanoliveiraa.monolito.Model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
