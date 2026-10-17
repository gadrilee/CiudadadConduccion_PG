package com.graphics.mundo;

/**
 * Decide si un vehículo circular puede moverse de un punto a otro.
 * Lo implementan las colisiones con la ciudad y el tráfico; se combinan con {@link #y}.
 */
@FunctionalInterface
public interface Transitable {

    boolean permite(float desdeX, float desdeZ, float haciaX, float haciaZ, float radio);

    /** Solo permite el movimiento si ambas reglas lo permiten. */
    default Transitable y(Transitable otra) {
        return (desdeX, desdeZ, haciaX, haciaZ, radio) ->
            permite(desdeX, desdeZ, haciaX, haciaZ, radio) && otra.permite(desdeX, desdeZ, haciaX, haciaZ, radio);
    }
}
