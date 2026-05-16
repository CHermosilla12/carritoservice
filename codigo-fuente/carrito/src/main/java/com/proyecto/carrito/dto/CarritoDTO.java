package com.proyecto.carrito.dto;

import java.util.List;
import com.proyecto.carrito.model.ItemCarrito;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoDTO {
    private Long id;
    private List<ItemCarrito> productos;

}
