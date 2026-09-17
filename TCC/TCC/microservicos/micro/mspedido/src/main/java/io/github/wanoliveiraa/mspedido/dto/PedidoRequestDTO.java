package io.github.wanoliveiraa.mspedido.dto;

import lombok.Data;
import java.util.List;

@Data
public class PedidoRequestDTO {
    private Long clienteId;
    private List<ItemPedidoRequestDTO> itens;
}
