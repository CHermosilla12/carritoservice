package com.proyecto.carrito.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoCreateDTO {
    @NotBlank(message = "La identificación del producto es obligatoria")
    private Long productoId;

    @NotBlank(message = "La cantidad es obligatoria")
    private int cantidad;

    @NotBlank(message = "El precio unitario es obligatorio")
    private double precioUnitario;

    public Integer getSubtotal() {
        return cantidad * (int) precioUnitario;
    }
}
