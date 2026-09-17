package io.github.wanoliveiraa.monolito.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private BigDecimal preco;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    @JsonIgnore // Ignora a serialização da lista de itens do produto
    private List<ItemPedido> itens;;
}
