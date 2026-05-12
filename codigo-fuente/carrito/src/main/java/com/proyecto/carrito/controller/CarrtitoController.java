package com.proyecto.carrito.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import com.proyecto.carrito.model.Carrito;
import com.proyecto.carrito.service.CarritoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/carrito")
public class CarrtitoController {
    private final CarritoService service;
    public CarrtitoController(CarritoService service) {
        this.service = service;
    }

    // 1. Listar todos los items del carrito
    @GetMapping
    public List<Carrito> listarTodo() {
        return service.obtenerCarrito();
    }

    // 2. Agregar un producto (o sumar cantidad si ya existe)
    @PostMapping
    public ResponseEntity<Carrito> agregarItem(@Valid @RequestBody Carrito item) {
        Carrito guardado = service.agregarProductoAlCarrito(item);
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }

    // 3. Eliminar un item específico por su ID de tabla
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long id) {
        service.eliminarProductoDelCarrito(id);
        return ResponseEntity.noContent().build();
    }

    // 4. Vaciar todo el carrito
    @DeleteMapping("/vaciar")
    public ResponseEntity<Void> vaciar() {
        service.EliminarTodoElCarrito();
        return ResponseEntity.noContent().build();
    }

    // 5. Obtener el Total de la compra
    @GetMapping("/total")
    public ResponseEntity<Map<String, Object>> obtenerTotal() {
        Integer total = service.calcularTotal();
        // Devolvemos un JSON con el total y un mensaje
        return ResponseEntity.ok(Map.of(
            "totalCompra", total,
            "moneda", "CLP",
            "mensaje", "Gracias por su preferencia"
        ));
    }
}
