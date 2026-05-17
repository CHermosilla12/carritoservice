package com.proyecto.carrito.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "La lista de productos no puede estar vacía")
    @ElementCollection
    @CollectionTable(name = "carrito_productos", joinColumns = @JoinColumn(name = "carrito_id"))
    private List<ItemCarrito> productos = new ArrayList<>();
}
