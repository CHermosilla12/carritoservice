package com.proyecto.carrito.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarrito {
    private Long productoId;
    private Integer cantidad;
}
