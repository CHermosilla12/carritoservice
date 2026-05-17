package com.proyecto.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarritoDTO {
    private Long productoId;
    private Integer cantidad;
}
