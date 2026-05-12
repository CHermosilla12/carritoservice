package com.proyecto.carrito.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.proyecto.carrito.dto.*;
@FeignClient(name = "Producto-Carrito", url = "${producto.service.url}")
public interface ProductoClient {

    @GetMapping("/api/productos/{id}")
    ProductoDTO getProductoDTObyID(@PathVariable("id") Long id );
    
}
