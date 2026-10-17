package com.graphics.vehiculo;

import com.graphics.mundo.Rectangulo;
import com.graphics.mundo.Transitable;

/**
 * Estado y física del auto del jugador. Ángulo 0 mira al norte (-Z);
 * el frente es (-sen(angulo), -cos(angulo)). Todo se integra con deltaTime.
 */
public class Auto {

    public static final float INICIO_X = -50;
    public static final float INICIO_Z = 50;
    /** Medio ancho del auto contando las ruedas (forma de colisión). */
    public static final float MEDIO_ANCHO = 1.0f;
    /** Medio largo del auto, de los faros a las luces traseras. */
    public static final float MEDIO_LARGO = 1.36f;

    private static final float ACELERACION = 9;
    private static final float RESISTENCIA = 0.7f;
    private static final float RESISTENCIA_FRENO = 7;
    private static final float VELOCIDAD_MAXIMA = 16;
    private static final float VELOCIDAD_REVERSA = -6;
    private static final float SENSIBILIDAD_GIRO = 0.11f;
    /** Fracción de la aceleración que queda al arrastrar la chata (el peso extra la reduce). */
    private static final float FACTOR_CARGA = 0.8f;

    /** Remolque enganchado, o {@code null} si el auto va solo. */
    private final Chata chata;
    private float x;
    private float z;
    private float angulo;
    private float velocidad;

    /** Auto sin remolque. */
    public Auto() {
        this(null);
    }

    /** Auto que arrastra {@code chata} (puede ser {@code null}). */
    public Auto(Chata chata) {
        this.chata = chata;
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
        if (chata != null) {
            chata.alinear(x, z, angulo);
        }
    }

    /**
     * Avanza la simulación {@code deltaTime} segundos. Si el destino del auto o de su chata
     * está ocupado, el auto se detiene.
     *
     * @return true si iba en movimiento y una colisión rechazó el paso
     */
    public boolean actualizar(float deltaTime, ControlesAuto controles, Transitable via) {
        float aceleracion = chata != null ? ACELERACION * FACTOR_CARGA : ACELERACION;
        velocidad += controles.acelerador() * aceleracion * deltaTime;
        float resistencia = controles.freno() ? RESISTENCIA_FRENO : RESISTENCIA;
        // Decaimiento exponencial: la fracción conservada depende del tiempo, no de los fps.
        velocidad *= (float) Math.exp(-resistencia * deltaTime);
        velocidad = Math.max(VELOCIDAD_REVERSA, Math.min(VELOCIDAD_MAXIMA, velocidad));

        // El giro es proporcional a la velocidad; en reversa se invierte como en un auto real.
        float anguloAnterior = angulo;
        angulo += controles.direccion() * velocidad * SENSIBILIDAD_GIRO * deltaTime;

        float siguienteX = x + frenteX() * velocidad * deltaTime;
        float siguienteZ = z + frenteZ() * velocidad * deltaTime;
        // El paso solo se acepta si caben los dos: un choque de la chata también frena al auto.
        boolean autoLibre = via.permite(forma(x, z, anguloAnterior), forma(siguienteX, siguienteZ, angulo));
        boolean chataLibre = chata == null || chata.puedeSeguir(siguienteX, siguienteZ, angulo, via);
        if (autoLibre && chataLibre) {
            x = siguienteX;
            z = siguienteZ;
            if (chata != null) {
                chata.seguir(x, z, angulo);
            }
            return false;
        }
        // Se deshace también el giro: girar en el sitio podría meter una esquina en el obstáculo.
        angulo = anguloAnterior;
        boolean choque = Math.abs(velocidad) > 0.05f;
        velocidad = 0;
        return choque;
    }

    /** Rectángulo de colisión del auto en su posición actual. */
    public Rectangulo forma() {
        return forma(x, z, angulo);
    }

    /** Rectángulo de colisión que tendría el auto en (x, z) girado {@code angulo}. */
    public static Rectangulo forma(float x, float z, float angulo) {
        return new Rectangulo(x, z, angulo, MEDIO_ANCHO, MEDIO_LARGO);
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

    /** Remolque enganchado, o {@code null} si no tiene. */
    public Chata chata() {
        return chata;
    }
}
