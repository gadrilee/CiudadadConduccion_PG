package com.graphics.juego;

/**
 * Objetivo de la partida: llegar a cada destino en orden y frenar sobre él.
 * Una entrega se completa a menos de 3 unidades del destino y a menos de 1 unidad/s.
 */
public class Entregas {

    /** Destinos (X, Z) en cruces de calles de cada sector: oeste, norte, este, centro y sur. */
    public static final float[][] DESTINOS = {
        {-50, -10},
        {10, -50},
        {50, 10},
        {10, -10},
        {30, 50}
    };

    private static final float RADIO_LLEGADA = 3;
    private static final float VELOCIDAD_MAXIMA_ENTREGA = 1;

    private int completadas;
    private float tiempo;

    /** Avanza el cronómetro y devuelve true si en este cuadro se completó una entrega. */
    public boolean actualizar(float deltaTime, float x, float z, float velocidad) {
        if (completo()) {
            return false;
        }
        tiempo += deltaTime;
        float[] destino = DESTINOS[completadas];
        float distanciaX = x - destino[0];
        float distanciaZ = z - destino[1];
        boolean cerca = distanciaX * distanciaX + distanciaZ * distanciaZ < RADIO_LLEGADA * RADIO_LLEGADA;
        boolean frenado = Math.abs(velocidad) < VELOCIDAD_MAXIMA_ENTREGA;
        if (cerca && frenado) {
            completadas++;
            return true;
        }
        return false;
    }

    public void reiniciar() {
        completadas = 0;
        tiempo = 0;
    }

    public boolean completo() {
        return completadas >= DESTINOS.length;
    }

    /** Destino activo o {@code null} si ya no quedan entregas. */
    public float[] destinoActual() {
        return completo() ? null : DESTINOS[completadas];
    }

    public int completadas() {
        return completadas;
    }

    public int total() {
        return DESTINOS.length;
    }

    public float tiempo() {
        return tiempo;
    }
}
