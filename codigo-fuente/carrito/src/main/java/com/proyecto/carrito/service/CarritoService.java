package com.proyecto.carrito.service;
import java.util.List;
import java.util.Optional;


import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.proyecto.carrito.repository.CarritoRepository;
import com.proyecto.carrito.Client.ProductoClient;
import com.proyecto.carrito.dto.CarritoCreateDTO;
import com.proyecto.carrito.dto.CarritoDTO;
import com.proyecto.carrito.model.Carrito;


@Service
public class CarritoService {
    private static final Logger log = LoggerFactory.getLogger(CarritoService.class);

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ProductoClient productoClient;

    public List<Carrito> obtenerCarrito() {
        return carritoRepository.findAll();
    }

    //public CarritoDTO crearCarritoDTO(CarritoCreateDTO request){
    //    log.info("",request.getProductoId());


    //}



    public Carrito agregarProductoAlCarrito(Carrito carrito) {
        Optional<Carrito> carritoExistente = carritoRepository.findAll().stream()
                .filter(c -> c.getProductoId().equals(carrito.getProductoId()))
                .findFirst();
        if (carritoExistente.isPresent()) {
            Carrito carritoActualizado = carritoExistente.get();
            carritoActualizado.setCantidad(carritoActualizado.getCantidad() + carrito.getCantidad());
            return carritoRepository.save(carritoActualizado);
        } else {
            return carritoRepository.save(carrito); 
    }
    }


    public void eliminarProductoDelCarrito(Long id) {
        if (carritoRepository.existsById(id)) {
            carritoRepository.deleteById(id);
        } 
    }
    
    public void EliminarTodoElCarrito() {
        carritoRepository.deleteAll();
    }

    public Integer calcularTotal() {
        List<Carrito> carrito = carritoRepository.findAll();
        return carrito.stream()
                .mapToInt(Carrito::getSubtotal)
                .sum();
    }


}
