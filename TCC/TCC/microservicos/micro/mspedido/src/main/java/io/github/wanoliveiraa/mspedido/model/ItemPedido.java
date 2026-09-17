package io.github.wanoliveiraa.mspedido.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_pedido_seq")
    @SequenceGenerator(name = "item_pedido_seq", sequenceName = "item_pedido_sequence", allocationSize = 50)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    @JsonBackReference // Evita a recursão de serialização para o lado do Pedido
    private Pedido pedido;

    @Column(name = "produto_id")
    private Long produtoId;

    @Transient
    private DadosProduto produto;
}
