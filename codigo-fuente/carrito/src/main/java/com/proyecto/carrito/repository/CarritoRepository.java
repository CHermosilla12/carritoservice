package com.proyecto.carrito.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.carrito.model.Carrito;


@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    
}
