package io.github.wanoliveiraa.monolito.Model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Data
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pedido_seq")
    @SequenceGenerator(name = "pedido_seq", sequenceName = "pedido_sequence", allocationSize = 50)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "cliente_id")
    @JsonIgnoreProperties("pedidos") // Ignora a lista de pedidos no Cliente para evitar recursão
    private Cliente cliente;

    private LocalDate data;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonManagedReference // Para controlar a serialização dos itens
    private List<ItemPedido> itens;
}
