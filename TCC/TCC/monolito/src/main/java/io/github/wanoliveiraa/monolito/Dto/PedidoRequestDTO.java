package io.github.wanoliveiraa.monolito.Dto;

import lombok.Data;
import java.util.List;

@Data
public class PedidoRequestDTO {
    private Long clienteId;
    private List<ItemPedidoRequestDTO> itens;
}
