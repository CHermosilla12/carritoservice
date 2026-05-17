package com.proyecto.carrito.exeption;

public class NoDisponibleException extends RuntimeException {
    public NoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
