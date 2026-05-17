package com.proyecto.carrito.dto;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class CarritoCreateDTO {
    private Long productoId;
    private Integer cantidad;
}
