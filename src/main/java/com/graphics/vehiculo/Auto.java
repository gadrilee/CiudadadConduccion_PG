package com.graphics.vehiculo;

import com.graphics.mundo.Transitable;

/**
 * Estado y física del auto del jugador. Ángulo 0 mira al norte (-Z);
 * el frente es (-sen(angulo), -cos(angulo)). Todo se integra con deltaTime.
 */
public class Auto {

    public static final float INICIO_X = -50;
    public static final float INICIO_Z = 50;
    /** Radio del círculo que contiene todas las piezas del auto. */
    public static final float RADIO = 1.65f;

    private static final float ACELERACION = 9;
    private static final float RESISTENCIA = 0.7f;
    private static final float RESISTENCIA_FRENO = 7;
    private static final float VELOCIDAD_MAXIMA = 16;
    private static final float VELOCIDAD_REVERSA = -6;
    private static final float SENSIBILIDAD_GIRO = 0.11f;

    private float x;
    private float z;
    private float angulo;
    private float velocidad;

    public Auto() {
        reiniciar();
    }

    /** Vuelve al punto de partida, mirando al norte y detenido. */
    public void reiniciar() {
        colocar(INICIO_X, INICIO_Z, 0, 0);
    }

    /** Coloca el auto en un estado concreto (usado por el reinicio y las pruebas). */
    public void colocar(float x, float z, float angulo, float velocidad) {
        this.x = x;
        this.z = z;
        this.angulo = angulo;
        this.velocidad = velocidad;
    }

    /**
     * Avanza la simulación {@code deltaTime} segundos. Si el destino está ocupado, el auto se detiene.
     *
     * @return true si iba en movimiento y una colisión rechazó el paso
     */
    public boolean actualizar(float deltaTime, ControlesAuto controles, Transitable via) {
        velocidad += controles.acelerador() * ACELERACION * deltaTime;
        float resistencia = controles.freno() ? RESISTENCIA_FRENO : RESISTENCIA;
        // Decaimiento exponencial: la fracción conservada depende del tiempo, no de los fps.
        velocidad *= (float) Math.exp(-resistencia * deltaTime);
        velocidad = Math.max(VELOCIDAD_REVERSA, Math.min(VELOCIDAD_MAXIMA, velocidad));

        // El giro es proporcional a la velocidad; en reversa se invierte como en un auto real.
        angulo += controles.direccion() * velocidad * SENSIBILIDAD_GIRO * deltaTime;

        float siguienteX = x + frenteX() * velocidad * deltaTime;
        float siguienteZ = z + frenteZ() * velocidad * deltaTime;
        if (via.permite(x, z, siguienteX, siguienteZ, RADIO)) {
            x = siguienteX;
            z = siguienteZ;
            return false;
        }
        boolean choque = Math.abs(velocidad) > 0.05f;
        velocidad = 0;
        return choque;
    }

    public float frenteX() {
        return -(float) Math.sin(angulo);
    }

    public float frenteZ() {
        return -(float) Math.cos(angulo);
    }

    /** Velocidad en km/h suponiendo 1 unidad = 1 metro. */
    public int kilometrosPorHora() {
        return Math.round(Math.abs(velocidad) * 3.6f);
    }

    public float x() {
        return x;
    }

    public float z() {
        return z;
    }

    public float angulo() {
        return angulo;
    }

    public float velocidad() {
        return velocidad;
    }
}
