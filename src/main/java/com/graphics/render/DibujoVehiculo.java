package com.graphics.render;

/** Modelo de auto hecho con cajas; lo comparten el jugador y el tráfico, cambiando el color. */
public class DibujoVehiculo {

    private static final float[] LADOS_RUEDAS = {-0.88f, 0.88f};
    private static final float[] EJES_RUEDAS = {-0.82f, 0.82f};
    private static final float[] LADOS_FAROS = {-0.55f, 0.55f};
    private static final float[] LADOS_CHATA = {-0.63f, 0.63f};
    private static final float[] ALTURAS_TABLAS = {0.72f, 0.98f};
    private static final float[] POSTES_CHATA = {-0.98f, 0, 0.98f};
    private static final float[] MADERA = {0.58f, 0.38f, 0.2f};
    private static final float[] ROJO = {0.78f, 0.12f, 0.1f};

    private final Renderizador render;

    public DibujoVehiculo(Renderizador render) {
        this.render = render;
    }

    /**
     * Dibuja un auto en (x, z) girado {@code angulo}. Las piezas usan coordenadas locales
     * (frente en -Z) y {@link Renderizador#pieza} las gira y traslada juntas.
     */
    public void dibujar(float x, float z, float angulo, float rojo, float verde, float azul, boolean farosEncendidos) {
        render.pieza(x, z, angulo, 0, 0.65f, 0, 1.65f, 0.55f, 2.6f, rojo, verde, azul);
        render.pieza(x, z, angulo, 0, 1.12f, 0.12f, 1.3f, 0.55f, 1.25f, 0.22f, 0.65f, 0.78f);
        for (float lado : LADOS_RUEDAS) {
            for (float eje : EJES_RUEDAS) {
                render.pieza(x, z, angulo, lado, 0.38f, eje, 0.24f, 0.58f, 0.6f, 0.055f, 0.065f, 0.08f);
            }
        }
        for (float lado : LADOS_FAROS) {
            render.emision(farosEncendidos);
            render.pieza(x, z, angulo, lado, 0.68f, -1.32f, 0.38f, 0.2f, 0.07f, 1, 0.95f, 0.65f);
            render.emision(false);
            render.pieza(x, z, angulo, lado, 0.68f, 1.32f, 0.35f, 0.17f, 0.07f, 0.85f, 0.05f, 0.05f);
        }
    }

    /**
     * Dibuja la chata (remolque de madera con marco rojo) en (x, z) girada {@code angulo}.
     * El origen local es el centro de la caja; la lanza llega hasta el enganche en -Z.
     */
    public void dibujarChata(float x, float z, float angulo, boolean lucesEncendidas) {
        // Piso de madera y marco rojo debajo.
        render.pieza(x, z, angulo, 0, 0.55f, 0, 1.3f, 0.08f, 2.0f, MADERA[0], MADERA[1], MADERA[2]);
        for (float lado : LADOS_CHATA) {
            render.pieza(x, z, angulo, lado, 0.46f, 0, 0.08f, 0.1f, 2.05f, ROJO[0], ROJO[1], ROJO[2]);
        }
        // Barandas: dos tablas por lado (con hueco entre ellas) y un borde rojo arriba.
        for (float altura : ALTURAS_TABLAS) {
            for (float lado : LADOS_CHATA) {
                render.pieza(x, z, angulo, lado, altura, 0, 0.05f, 0.2f, 2.0f, MADERA[0], MADERA[1], MADERA[2]);
                render.pieza(x, z, angulo, 0, altura, lado * 1.55f, 1.3f, 0.2f, 0.05f,
                    MADERA[0], MADERA[1], MADERA[2]);
            }
        }
        for (float lado : LADOS_CHATA) {
            render.pieza(x, z, angulo, lado, 1.14f, 0, 0.07f, 0.06f, 2.06f, ROJO[0], ROJO[1], ROJO[2]);
            render.pieza(x, z, angulo, 0, 1.14f, lado * 1.55f, 1.36f, 0.06f, 0.07f, ROJO[0], ROJO[1], ROJO[2]);
            for (float poste : POSTES_CHATA) {
                render.pieza(x, z, angulo, lado * 1.03f, 0.85f, poste, 0.07f, 0.62f, 0.07f, ROJO[0], ROJO[1], ROJO[2]);
            }
        }
        // Eje con dos ruedas, cada una con su aro y guardabarros rojo.
        render.pieza(x, z, angulo, 0, 0.35f, 0.1f, 1.7f, 0.08f, 0.08f, 0.2f, 0.2f, 0.22f);
        for (float lado : LADOS_CHATA) {
            float ladoRueda = lado * 1.3f;
            render.pieza(x, z, angulo, ladoRueda, 0.35f, 0.1f, 0.22f, 0.7f, 0.7f, 0.055f, 0.065f, 0.08f);
            render.pieza(x, z, angulo, ladoRueda * 1.13f, 0.35f, 0.1f, 0.03f, 0.32f, 0.32f, 0.6f, 0.6f, 0.62f);
            render.pieza(x, z, angulo, ladoRueda, 0.75f, 0.1f, 0.3f, 0.05f, 0.85f, ROJO[0], ROJO[1], ROJO[2]);
        }
        // Lanza roja hasta el enganche y acople metálico que se apoya en el auto.
        render.pieza(x, z, angulo, 0, 0.45f, -1.45f, 0.12f, 0.1f, 0.9f, ROJO[0], ROJO[1], ROJO[2]);
        render.pieza(x, z, angulo, 0, 0.47f, -1.92f, 0.18f, 0.14f, 0.3f, 0.7f, 0.7f, 0.72f);
        // Luces traseras: se encienden con los faros.
        for (float lado : LADOS_FAROS) {
            render.emision(lucesEncendidas);
            render.pieza(x, z, angulo, lado * 0.9f, 0.48f, 1.04f, 0.22f, 0.08f, 0.04f, 0.9f, 0.05f, 0.05f);
            render.emision(false);
        }
    }
}
