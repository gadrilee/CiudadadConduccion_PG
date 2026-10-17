package com.graphics.vehiculo;

import com.graphics.mundo.Rectangulo;
import com.graphics.mundo.Transitable;

/**
 * Chata (remolque de 1.3 x 2 m) enganchada detrás del auto. Se modela como una barra rígida:
 * el enganche va fijo en la parte trasera del auto y la chata es arrastrada hacia él manteniendo
 * siempre la misma distancia. Usa la misma convención de ángulo que {@link Auto}.
 */
public class Chata {

    /** Distancia del centro del auto a la bola de enganche (justo detrás del parachoques). */
    public static final float DISTANCIA_ENGANCHE = 1.5f;
    /** Distancia de la bola de enganche al centro de la chata (lanza + media caja). */
    public static final float LARGO_LANZA = 1.9f;
    /** Medio ancho de la caja contando ruedas y guardabarros (forma de colisión; la lanza no cuenta). */
    public static final float MEDIO_ANCHO = 0.97f;
    /** Medio largo de la caja de carga, con las luces traseras. */
    public static final float MEDIO_LARGO = 1.06f;
    /** Ángulo máximo entre auto y chata (unos 70°): evita que se doble sobre el auto. */
    public static final float ANGULO_MAXIMO = 1.22f;

    private float x;
    private float z;
    private float angulo;

    /** Coloca la chata recta detrás del auto (inicio y reinicio). */
    public void alinear(float autoX, float autoZ, float anguloAuto) {
        angulo = anguloAuto;
        x = engancheX(autoX, anguloAuto) + (float) Math.sin(angulo) * LARGO_LANZA;
        z = engancheZ(autoZ, anguloAuto) + (float) Math.cos(angulo) * LARGO_LANZA;
    }

    /** ¿Puede la chata acompañar al auto hasta esa posición sin chocar? No la mueve. */
    public boolean puedeSeguir(float autoX, float autoZ, float anguloAuto, Transitable via) {
        float[] siguiente = calcular(autoX, autoZ, anguloAuto);
        return via.permite(forma(), new Rectangulo(siguiente[0], siguiente[1], siguiente[2], MEDIO_ANCHO, MEDIO_LARGO));
    }

    /** Rectángulo de colisión de la caja de carga. */
    public Rectangulo forma() {
        return new Rectangulo(x, z, angulo, MEDIO_ANCHO, MEDIO_LARGO);
    }

    /** Arrastra la chata detrás del auto ya movido. */
    public void seguir(float autoX, float autoZ, float anguloAuto) {
        float[] siguiente = calcular(autoX, autoZ, anguloAuto);
        x = siguiente[0];
        z = siguiente[1];
        angulo = siguiente[2];
    }

    /**
     * Posición y ángulo que tendría la chata con el auto en (autoX, autoZ).
     *
     * @return {x, z, angulo}
     */
    private float[] calcular(float autoX, float autoZ, float anguloAuto) {
        float puntoX = engancheX(autoX, anguloAuto);
        float puntoZ = engancheZ(autoZ, anguloAuto);
        // La lanza apunta de la chata al enganche: esa dirección es el nuevo frente (-sen a, -cos a).
        float nuevoAngulo = (float) Math.atan2(-(puntoX - x), -(puntoZ - z));
        // Diferencia con el auto llevada a [-PI, PI] y limitada para que no se cruce con él.
        float diferencia = (float) Math.atan2(Math.sin(nuevoAngulo - anguloAuto), Math.cos(nuevoAngulo - anguloAuto));
        diferencia = Math.max(-ANGULO_MAXIMO, Math.min(ANGULO_MAXIMO, diferencia));
        nuevoAngulo = anguloAuto + diferencia;
        // El centro queda a LARGO_LANZA del enganche, hacia atrás de la chata: la barra nunca se estira.
        float nuevoX = puntoX + (float) Math.sin(nuevoAngulo) * LARGO_LANZA;
        float nuevoZ = puntoZ + (float) Math.cos(nuevoAngulo) * LARGO_LANZA;
        return new float[] {nuevoX, nuevoZ, nuevoAngulo};
    }

    /** El enganche está detrás del auto: opuesto a su frente (-sen a, -cos a). */
    private static float engancheX(float autoX, float anguloAuto) {
        return autoX + (float) Math.sin(anguloAuto) * DISTANCIA_ENGANCHE;
    }

    private static float engancheZ(float autoZ, float anguloAuto) {
        return autoZ + (float) Math.cos(anguloAuto) * DISTANCIA_ENGANCHE;
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
}
