package io.github.wanoliveiraa.monolito.Infra;


import io.github.wanoliveiraa.monolito.Model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido,Long> {

    @Query("SELECT p FROM Pedido p JOIN FETCH p.itens i JOIN FETCH i.produto pr JOIN FETCH p.cliente c WHERE c.id = :clienteId")
    List<Pedido> findPedidosPorCliente(@Param("clienteId") Long clienteId);

    @Query("SELECT p FROM Pedido p JOIN FETCH p.itens i JOIN FETCH i.produto pr JOIN FETCH p.cliente c WHERE p.id = :id")
    Optional<Pedido> findByIdComItens(@Param("id") Long id);
}
