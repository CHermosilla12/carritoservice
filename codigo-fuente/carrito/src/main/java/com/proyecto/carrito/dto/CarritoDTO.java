package com.proyecto.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoDTO {
    private Long id;
    private Long productoId;
    private int cantidad;
    private double precioUnitario;

    public Integer getSubtotal() {
        return cantidad * (int) precioUnitario;
    }
}
