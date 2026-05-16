package com.proyecto.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer precio;
    private Integer cantidad;
    private Integer subtotal;
    
    public void calcularSubtotal() {
        if (this.precio != null && this.cantidad != null) {
            this.subtotal = this.precio * this.cantidad;
        } else {
            this.subtotal = 0;
        }
    }
}
