package com.example.ap2.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Pedido {
    private Long id;
    private LocalDateTime dataPedido;
    private String statusPedido;

    //relacionamentos
    private Cliente cliente;
    private List<Produto> produtos;

}
