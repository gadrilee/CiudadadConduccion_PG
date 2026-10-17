package com.graphics.render;

/** Modelo de auto hecho con cajas; lo comparten el jugador y el tráfico, cambiando el color. */
public class DibujoVehiculo {

    private static final float[] LADOS_RUEDAS = {-0.88f, 0.88f};
    private static final float[] EJES_RUEDAS = {-0.82f, 0.82f};
    private static final float[] LADOS_FAROS = {-0.55f, 0.55f};

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
}
