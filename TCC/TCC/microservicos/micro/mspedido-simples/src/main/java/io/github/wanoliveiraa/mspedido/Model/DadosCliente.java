package io.github.wanoliveiraa.mspedido.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DadosCliente {

    private Long id;

    private String nome;

    private String email;


}
