package com.proyecto.carrito.service;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.proyecto.carrito.repository.CarritoRepository;

import feign.FeignException;

import com.proyecto.carrito.Client.ProductoClient;
import com.proyecto.carrito.dto.CarritoCreateDTO;
import com.proyecto.carrito.dto.CarritoDTO;
import com.proyecto.carrito.dto.ProductoDTO;
import com.proyecto.carrito.model.Carrito;
import com.proyecto.carrito.model.ItemCarrito;

import java.util.stream.Collectors;

@Service
public class CarritoService {
    private static final Logger log = LoggerFactory.getLogger(CarritoService.class);

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ProductoClient productoClient;

    public List<CarritoDTO> obtenerCarrito() {
        log.info("Obteniendo todos los carritos de compras");
        return carritoRepository.findAll().stream()
                .map(this::convertirACarritoDTO)
                .collect(Collectors.toList());
    }

    public ProductoDTO obtenerProductoDelCarrito(Long id, Long productoId) {
        log.info("Obteniendo detalle de producto del carrito con ID: {} y producto ID: {}", id, productoId);

        Carrito carro = carritoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        ItemCarrito itemLocal = carro.getProductos().stream()
                .filter(item -> item.getProductoId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));

        ProductoDTO productoCatalogo = verificarProducto(productoId);

        productoCatalogo.setCantidad(itemLocal.getCantidad());
        productoCatalogo.calcularSubtotal();

        return productoCatalogo;
    }

    public CarritoDTO crearCarritoDTO(CarritoCreateDTO carrito) {
        log.info("Agregando producto al carrito: {}", carrito);
        verificarProducto(carrito.getProductoId());
        Carrito nuevoCarrito = new Carrito();
        ItemCarrito nuevoItem = new ItemCarrito();
        nuevoItem.setProductoId(carrito.getProductoId());
        nuevoItem.setCantidad(carrito.getCantidad());
        nuevoCarrito.getProductos().add(nuevoItem);
        Carrito carritoGuardado = carritoRepository.save(nuevoCarrito);
        log.info("Producto agregado al carrito con ID: {}", carritoGuardado.getId());

        return convertirACarritoDTO(carritoGuardado);
    }

    public void eliminarProductoDelCarrito(Long id) {
        log.info("Eliminando carrito con ID: {}", id);
        if (carritoRepository.existsById(id)) {
            carritoRepository.deleteById(id);
        } 
    }
    
    public void EliminarTodoElCarrito() {
        log.warn("Vaciando por completo la tabla de carritos");
        carritoRepository.deleteAll();
    }

    public Integer calcularTotalDelCarrito(Long id) {
        log.info("Calculando el costo total del carrito con ID: {}", id);
        Carrito carrito = carritoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        return carrito.getProductos().stream()
            .mapToInt(item -> {
                try {
                    ProductoDTO prod = verificarProducto(item.getProductoId());
                    return prod.getPrecio() * item.getCantidad();
                } catch (Exception e) {
                    log.error("No se pudo obtener el precio para el producto ID {} en el carrito {}", 
                            item.getProductoId(), id);
                    return 0; 
                }
            })
            .sum();
    }

    public CarritoDTO actualizarCantidad(Long id, Long productoId, int cantidad) {
        log.info("Actualizando cantidad del producto ID {} en el carrito ID {} a {}", productoId, id, cantidad);

        Carrito carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        ItemCarrito item = carrito.getProductos().stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));

        item.setCantidad(cantidad);
        Carrito carritoActualizado = carritoRepository.save(carrito);
        log.info("Cantidad actualizada para producto ID {} en el carrito ID {}", productoId, id);

        return convertirACarritoDTO(carritoActualizado);
    }

    private CarritoDTO convertirACarritoDTO(Carrito carrito) {
        CarritoDTO carritoDTO = new CarritoDTO();
        carritoDTO.setId(carrito.getId());
        carritoDTO.setProductos(carrito.getProductos()); 
        return carritoDTO;
    }

    private ProductoDTO verificarProducto(Long productoId) {
        try {
            log.info("Comunicándose con Catálogo para verificar producto ID: {}", productoId);
            return productoClient.getProductoDTObyID(productoId);
        } catch (FeignException.NotFound e) {
            log.error("Producto con ID {} no existe en el Catálogo", productoId);
            throw new RuntimeException("Producto no encontrado en el Catálogo");
        } catch (FeignException e) {
            log.error("Error de comunicación por Feign al verificar producto: {}", e.getMessage());
            throw new RuntimeException("Error al conectar con el servicio de Catálogo");
        }
    }
}
