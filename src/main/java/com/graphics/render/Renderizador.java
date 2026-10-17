package com.graphics.render;

/**
 * Primitivas de dibujo 3D: todo objeto de la ciudad es una caja (el cubo unitario transformado).
 * Orden de argumentos: posición X/Y/Z, tamaño X/Y/Z, color R/G/B (0 a 1) y, si aplica, el ángulo.
 */
public class Renderizador {

    private final ProgramaShader programa;
    private final MallaCubo cubo;
    private boolean modoMapa;

    public Renderizador() {
        programa = new ProgramaShader("/shaders/escena.vert", "/shaders/escena.frag");
        cubo = new MallaCubo();
    }

    /** Activa el shader de escena y el cubo; se llama al empezar cada pase 3D. */
    public void comenzar() {
        programa.usar();
        cubo.enlazar();
    }

    public ProgramaShader programa() {
        return programa;
    }

    /** Caja sin giro. */
    public void caja(float x, float y, float z, float sx, float sy, float sz, float r, float g, float b) {
        cajaGirada(x, y, z, sx, sy, sz, r, g, b, 0);
    }

    /** Caja girada {@code angulo} radianes alrededor del eje Y. */
    public void cajaGirada(float x, float y, float z, float sx, float sy, float sz,
                           float r, float g, float b, float angulo) {
        programa.vector3("uPos", x, y, z);
        programa.vector3("uEscala", sx, sy, sz);
        programa.vector3("uColor", r, g, b);
        programa.decimal("uGiro", angulo);
        cubo.dibujar();
    }

    /**
     * Dibuja una pieza definida en coordenadas locales de un objeto (por ejemplo, un auto)
     * que está en (origenX, origenZ) y girado {@code angulo}. Así las piezas giran juntas.
     */
    public void pieza(float origenX, float origenZ, float angulo,
                      float x, float y, float z, float sx, float sy, float sz, float r, float g, float b) {
        float coseno = (float) Math.cos(angulo);
        float seno = (float) Math.sin(angulo);
        // Misma rotación que el vertex shader, aplicada al desplazamiento local.
        float mundoX = origenX + coseno * x + seno * z;
        float mundoZ = origenZ - seno * x + coseno * z;
        cajaGirada(mundoX, y, mundoZ, sx, sy, sz, r, g, b, angulo);
    }

    /** Los objetos emisivos (bombillas, ventanas encendidas) se ven con su color pero no iluminan. */
    public void emision(boolean activa) {
        programa.entero("uEmision", activa ? 1 : 0);
    }

    /** Indica si se está dibujando el minimapa, para omitir detalles pequeños. */
    public boolean modoMapa() {
        return modoMapa;
    }

    public void setModoMapa(boolean modoMapa) {
        this.modoMapa = modoMapa;
        programa.entero("uMapa", modoMapa ? 1 : 0);
    }

    public void eliminar() {
        cubo.eliminar();
        programa.eliminar();
    }
}
