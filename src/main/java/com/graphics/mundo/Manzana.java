package com.graphics.mundo;

/**
 * Manzana (celda no transitable) ya convertida a coordenadas del mundo.
 * {@code altura} solo tiene sentido para edificios; el color es el de la fachada.
 */
public record Manzana(int fila, int columna, TipoCelda tipo, float x, float z,
                      float altura, float rojo, float verde, float azul, Sector sector) {

    public boolean esEdificio() {
        return tipo == TipoCelda.EDIFICIO;
    }

    public boolean esParque() {
        return tipo == TipoCelda.PARQUE;
    }

    /** Índice de la manzana dentro de la cuadrícula de manzanas (0, 1, 2...), útil para alternar adornos. */
    public int indiceFila() {
        return fila / 2;
    }

    public int indiceColumna() {
        return columna / 2;
    }
}
