package com.graphics.vehiculo;

/**
 * Órdenes del conductor en un cuadro.
 *
 * @param acelerador 1 avanza, -1 frena/retrocede, 0 suelta el pedal
 * @param direccion  1 gira a la izquierda, -1 a la derecha, 0 recto
 * @param freno      true mientras se mantiene el freno de mano
 */
public record ControlesAuto(float acelerador, float direccion, boolean freno) {

    public static final ControlesAuto NINGUNO = new ControlesAuto(0, 0, false);
}
