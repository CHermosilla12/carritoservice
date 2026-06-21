package com.proyecto.carrito.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Schema(description = "Datos para crear un carrito")
public class CarritoCreateDTO {
    
    @Schema(description = "id de producto", example = "7")
    private Long productoId;

    @Schema(description = "cantidad", example = "10")
    private Integer cantidad;
}
