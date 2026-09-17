package io.github.wanoliveiraa.mspedido.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DadosCliente {

    private Long id;

    private String nome;

    private String email;


}
