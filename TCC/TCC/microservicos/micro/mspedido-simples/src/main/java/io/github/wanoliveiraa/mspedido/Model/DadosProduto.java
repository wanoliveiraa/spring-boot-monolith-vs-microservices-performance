package io.github.wanoliveiraa.mspedido.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DadosProduto {

    private Long id;
    private String nome;
    private BigDecimal preco;

}
