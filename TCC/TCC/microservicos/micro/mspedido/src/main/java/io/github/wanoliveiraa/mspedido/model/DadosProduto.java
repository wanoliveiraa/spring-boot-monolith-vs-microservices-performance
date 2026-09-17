package io.github.wanoliveiraa.mspedido.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DadosProduto {

    private Long id;
    private String nome;
    private BigDecimal preco;

}
