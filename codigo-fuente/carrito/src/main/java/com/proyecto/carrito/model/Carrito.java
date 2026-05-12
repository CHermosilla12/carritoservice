package com.proyecto.carrito.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.GenerationType;



@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
