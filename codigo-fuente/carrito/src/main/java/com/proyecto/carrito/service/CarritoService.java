package com.proyecto.carrito.service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.proyecto.carrito.repository.CarritoRepository;
import com.proyecto.carrito.dto.*;
import feign.FeignException;
import com.proyecto.carrito.Client.ProductoClient;
import com.proyecto.carrito.model.Carrito;
import com.proyecto.carrito.model.ItemCarrito;
import com.proyecto.carrito.exeption.*;
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
                .orElseThrow(() -> new NoEncontradoException("Carrito no encontrado"));
        ItemCarrito itemLocal = carro.getProductos().stream()
                .filter(item -> item.getProductoId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new NoEncontradoException("Producto no encontrado en el carrito"));

        ProductoDTO productoCatalogo = verificarProducto(productoId);

        productoCatalogo.setCantidad(itemLocal.getCantidad());
        productoCatalogo.calcularSubtotal();

        return productoCatalogo;
    }

    public CarritoDTO crearCarritoDTO(List<CarritoCreateDTO> listaProductos) {
        log.info("Iniciando creación masiva de carrito con {} productos", listaProductos.size());
        Carrito nuevoCarrito = new Carrito();
        if (nuevoCarrito.getProductos() == null) {
            nuevoCarrito.setProductos(new java.util.ArrayList<>());
        }
        for (CarritoCreateDTO prodDto : listaProductos) {
            try {
                verificarProducto(prodDto.getProductoId());
                ItemCarrito nuevoItem = new ItemCarrito();
                nuevoItem.setProductoId(prodDto.getProductoId());
                nuevoItem.setCantidad(prodDto.getCantidad());
                nuevoCarrito.getProductos().add(nuevoItem);

            } catch (NoEncontradoException e) {
                // Si el producto explícitamente no existe, propagamos el 404 de inmediato
                log.error("Error al validar producto ID {} en la carga masiva", prodDto.getProductoId());
                throw e;
            } catch (NoDisponibleException e) {
                // Si el catálogo está apagado, propagamos el 503 directo al cliente
                throw e;
            } catch (Exception e) {
                log.error("Error inesperado en la carga masiva: {}", e.getMessage());
                throw new NoEncontradoException("No se pudo crear el carrito: El producto con ID "
                        + prodDto.getProductoId() + " no existe en el catálogo.");
            }
        }
        Carrito carritoGuardado = carritoRepository.save(nuevoCarrito);
        log.info("Carrito masivo guardado con éxito. ID asignado: {}", carritoGuardado.getId());
        return convertirACarritoDTO(carritoGuardado);
    }

    public void eliminarCarrito(Long id) {
        log.info("Eliminando carrito con ID: {}", id);
        if (carritoRepository.existsById(id)) {
            carritoRepository.deleteById(id);
        }
    }

    public CarritoDTO eliminarProductoDelCarrito(Long id, Long productoId) {
        log.info("Eliminando producto ID {} del carrito con ID: {}", productoId, id);
        Carrito carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Carrito no encontrado"));
        boolean removido = carrito.getProductos().removeIf(item -> item.getProductoId().equals(productoId));
        if (!removido) {
            throw new NoEncontradoException("El producto no se encontraba en el carrito");
        }
        Carrito carritoActualizado = carritoRepository.save(carrito);
        return convertirACarritoDTO(carritoActualizado);
    }

    public void EliminarTodoElCarrito() {
        log.warn("Vaciando por completo la tabla de carritos");
        carritoRepository.deleteAll();
    }

    public Integer calcularTotalDelCarrito(Long id) {
        log.info("Calculando el costo total del carrito con ID: {}", id);
        Carrito carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Carrito no encontrado"));
        return carrito.getProductos().stream()
                .mapToInt((ItemCarrito item) -> {
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
                .orElseThrow(() -> new NoEncontradoException("Carrito no encontrado"));
        ItemCarrito item = carrito.getProductos().stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new NoEncontradoException("Producto no encontrado en el carrito"));

        item.setCantidad(cantidad);
        Carrito carritoActualizado = carritoRepository.save(carrito);
        return convertirACarritoDTO(carritoActualizado);
    }

    private CarritoDTO convertirACarritoDTO(Carrito carrito) {
        CarritoDTO carritoDTO = new CarritoDTO();
        carritoDTO.setId(carrito.getId());

        List<ItemCarritoDetalleDTO> detalles = new ArrayList<>();

        if (carrito.getProductos() != null) {
            for (ItemCarrito item : carrito.getProductos()) {
                ItemCarritoDetalleDTO detalle = new ItemCarritoDetalleDTO();
                detalle.setProductoId(item.getProductoId());
                detalle.setCantidad(item.getCantidad());

                try {
                    ProductoDTO prodCatálogo = verificarProducto(item.getProductoId());

                    detalle.setNombre(prodCatálogo.getNombre());
                    detalle.setPrecio(prodCatálogo.getPrecio());
                    detalle.setSubtotal(prodCatálogo.getPrecio() * item.getCantidad());
                } catch (Exception e) {
                    log.error("No se pudo obtener detalle para el producto ID {}", item.getProductoId());
                    detalle.setNombre("Producto No Disponible");
                    detalle.setPrecio(0);
                    detalle.setSubtotal(0);
                }

                detalles.add(detalle);
            }
        }

        carritoDTO.setProductos(detalles);
        return carritoDTO;
    }

    private ProductoDTO verificarProducto(Long productoId) {
        try {
            log.info("Comunicándose con Catálogo para verificar producto ID: {}", productoId);
            return productoClient.getProductoDTObyID(productoId);
        } catch (FeignException.NotFound e) {
            log.error("Producto con ID {} no existe en el Catálogo", productoId);
            throw new NoEncontradoException("Producto no encontrado en el Catálogo con ID: " + productoId);
        } catch (FeignException e) {
            log.error("Error de comunicación por Feign al verificar producto: {}", e.getMessage());
            throw new NoDisponibleException(
                    "El servicio de Catálogo de productos no se encuentra disponible actualmente.");
        }
    }
}