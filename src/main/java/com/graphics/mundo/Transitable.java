package com.graphics.mundo;

/**
 * Decide si un vehículo (su rectángulo de colisión) puede pasar de una posición a otra.
 * Lo implementan las colisiones con la ciudad y el tráfico; se combinan con {@link #y}.
 */
@FunctionalInterface
public interface Transitable {

    boolean permite(Rectangulo desde, Rectangulo hacia);

    /** Solo permite el movimiento si ambas reglas lo permiten. */
    default Transitable y(Transitable otra) {
        return (desde, hacia) -> permite(desde, hacia) && otra.permite(desde, hacia);
    }
}
