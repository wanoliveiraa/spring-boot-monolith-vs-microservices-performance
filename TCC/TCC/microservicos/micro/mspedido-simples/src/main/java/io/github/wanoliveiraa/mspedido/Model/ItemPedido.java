package io.github.wanoliveiraa.mspedido.Model;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
