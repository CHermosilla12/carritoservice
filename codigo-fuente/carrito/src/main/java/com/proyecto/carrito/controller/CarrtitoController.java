package com.proyecto.carrito.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import com.proyecto.carrito.dto.*;
import com.proyecto.carrito.model.Carrito;
import com.proyecto.carrito.service.CarritoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Carrito", description = "CRUD de carrito de productos")
@RestController
@RequestMapping("/api/carrito")
public class CarrtitoController {

    private final CarritoService service;

    public CarrtitoController(CarritoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar carritos")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping
    public List<CarritoDTO> listarTodo() {
        return service.obtenerCarrito();
    }

    @Operation(summary = "Obtener carrito por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Carrito> obtenerCarrito(
            @Parameter(description = "ID del carrito", required = true) @PathVariable Long id) {
        Carrito carrito = service.obtenerPorId(id);
        return ResponseEntity.ok(carrito);
    }

    @Operation(summary = "Crear carrito")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping
    public ResponseEntity<CarritoDTO> CrearCarrito(
            @Valid @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Lista de productos") @RequestBody List<CarritoCreateDTO> carritos) {
        CarritoDTO carritoDTO = service.crearCarritoDTO(carritos);
        return new ResponseEntity<>(carritoDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener producto del carrito")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}/productos/{productoId}")
    public ResponseEntity<ProductoDTO> obtenerProductoDelCarrito(
            @Parameter(description = "ID del carrito", required = true) @PathVariable Long id, 
            @Parameter(description = "ID del producto", required = true) @PathVariable Long productoId) {
        ProductoDTO producto = service.obtenerProductoDelCarrito(id, productoId);
        return ResponseEntity.ok(producto);
    }

    @Operation(summary = "Actualizar cantidad de un producto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PutMapping("/{id}/productos/{productoId}")
    public ResponseEntity<CarritoDTO> actualizarCantidad(
            @Parameter(description = "ID del carrito", required = true) @PathVariable Long id,
            @Parameter(description = "ID del producto", required = true) @PathVariable Long productoId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Cuerpo con la cantidad") @RequestBody Map<String, Integer> body) {

        if (body == null || !body.containsKey("cantidad")) {
            throw new RuntimeException("El cuerpo de la petición debe contener el campo 'cantidad'");
        }

        int cantidad = body.get("cantidad");
        CarritoDTO carrito = service.actualizarCantidad(id, productoId, cantidad);
        return ResponseEntity.ok(carrito);
    }

    @Operation(summary = "Eliminar un carrito completo")
    @ApiResponse(responseCode = "204", description = "No Content")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItem(
            @Parameter(description = "ID del carrito", required = true) @PathVariable Long id) {
        service.eliminarCarrito(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar producto del carrito")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @DeleteMapping("/{id}/productos/{productoId}")
    public ResponseEntity<CarritoDTO> eliminarProducto(
            @Parameter(description = "ID del carrito", required = true) @PathVariable Long id,
            @Parameter(description = "ID del producto", required = true) @PathVariable Long productoId) {

        CarritoDTO carritoActualizado = service.eliminarProductoDelCarrito(id, productoId);
        return ResponseEntity.ok(carritoActualizado);
    }

    @Operation(summary = "Vaciar todos los carritos")
    @ApiResponse(responseCode = "204", description = "No Content")
    @DeleteMapping("/vaciar")
    public ResponseEntity<Void> vaciar() {
        service.EliminarTodoElCarrito();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Calcular total del carrito")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}/total")
    public ResponseEntity<Map<String, Object>> obtenerTotalDelCarrito(
            @Parameter(description = "ID del carrito", required = true) @PathVariable Long id) {
        Integer total = service.calcularTotalDelCarrito(id);

        return ResponseEntity.ok(Map.of(
                "carritoId", id,
                "totalCompra", total,
                "moneda", "CLP",
                "mensaje", "Total calculado"));
    }
}