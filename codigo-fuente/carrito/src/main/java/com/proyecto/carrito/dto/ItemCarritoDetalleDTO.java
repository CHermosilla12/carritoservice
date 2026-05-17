package com.proyecto.carrito.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarritoDetalleDTO {
    private Long productoId;
    private String nombre;
    private Integer precio;
    private Integer cantidad;
    private Integer subtotal;
}
