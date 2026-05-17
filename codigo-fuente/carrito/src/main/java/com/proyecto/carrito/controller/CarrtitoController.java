package com.proyecto.carrito.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import com.proyecto.carrito.dto.*;
import com.proyecto.carrito.service.CarritoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/carrito")
public class CarrtitoController {

    private final CarritoService service;

    public CarrtitoController(CarritoService service) {
        this.service = service;
    }

    // 1. Listar todos los carritos de la base de datos
    @GetMapping
    public List<CarritoDTO> listarTodo() {
        return service.obtenerCarrito();
    }

    // 2. Crear un nuevo carrito agregándole un producto inicial
    @PostMapping
    public ResponseEntity<CarritoDTO> agregarItem(@Valid @RequestBody List<CarritoCreateDTO> carritos) {
        CarritoDTO carritoDTO = service.crearCarritoDTO(carritos);
        return new ResponseEntity<>(carritoDTO, HttpStatus.CREATED);
    }

    // 3. Obtener el detalle completo de un producto específico dentro de un carrito
    @GetMapping("/{id}/productos/{productoId}")
    public ResponseEntity<ProductoDTO> obtenerProductoDelCarrito(@PathVariable Long id, @PathVariable Long productoId) {
        ProductoDTO producto = service.obtenerProductoDelCarrito(id, productoId);
        return ResponseEntity.ok(producto);
    }

    // 4. Actualizar la cantidad de copias de una carta en un carrito específico
    @PutMapping("/{id}/productos/{productoId}")
    public ResponseEntity<CarritoDTO> actualizarCantidad(
            @PathVariable Long id,
            @PathVariable Long productoId,
            @RequestBody Map<String, Integer> body) { // <- Cambiado a @RequestBody

        // Validamos que el JSON contenga la clave "cantidad" para evitar
        // NullPointerException
        if (body == null || !body.containsKey("cantidad")) {
            throw new RuntimeException("El cuerpo de la petición debe contener el campo 'cantidad'");
        }

        int cantidad = body.get("cantidad");

        // Tu service se queda exactamente igual como lo corregiste hoy
        CarritoDTO carrito = service.actualizarCantidad(id, productoId, cantidad);
        return ResponseEntity.ok(carrito);
    }

    // 5. Eliminar un carrito completo por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long id) {
        service.eliminarCarrito(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/productos/{productoId}")
    public ResponseEntity<CarritoDTO> eliminarProducto(
            @PathVariable Long id,
            @PathVariable Long productoId) {

        CarritoDTO carritoActualizado = service.eliminarProductoDelCarrito(id, productoId);
        return ResponseEntity.ok(carritoActualizado);
    }

    // 6. Vaciar absolutamente todos los carritos del sistema
    @DeleteMapping("/vaciar")
    public ResponseEntity<Void> vaciar() {
        service.EliminarTodoElCarrito();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/total")
    public ResponseEntity<Map<String, Object>> obtenerTotalDelCarrito(@PathVariable Long id) {
        Integer total = service.calcularTotalDelCarrito(id);

        return ResponseEntity.ok(Map.of(
                "carritoId", id,
                "totalCompra", total,
                "moneda", "CLP",
                "mensaje", "Total calculado para el carrito especificado"));
    }
}
