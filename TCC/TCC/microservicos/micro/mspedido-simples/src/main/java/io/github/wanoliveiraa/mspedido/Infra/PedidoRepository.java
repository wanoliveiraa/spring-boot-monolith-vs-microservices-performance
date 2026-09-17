package io.github.wanoliveiraa.mspedido.Infra;



import io.github.wanoliveiraa.mspedido.Model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido,Long> {
    List<Pedido> findPedidosByClienteId(Long clienteId);

    @Query("SELECT p FROM Pedido p JOIN FETCH p.itens")
    List<Pedido> findAllComItens();
}
