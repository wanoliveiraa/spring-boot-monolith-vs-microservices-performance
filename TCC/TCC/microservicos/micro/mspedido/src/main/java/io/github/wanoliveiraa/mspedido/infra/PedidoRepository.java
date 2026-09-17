package io.github.wanoliveiraa.mspedido.infra;



import io.github.wanoliveiraa.mspedido.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido,Long> {

    List<Pedido> findPedidosByClienteId(Long clienteId);

    @Query("SELECT p FROM Pedido p JOIN FETCH p.itens")
    List<Pedido> findAllComItens();
}
