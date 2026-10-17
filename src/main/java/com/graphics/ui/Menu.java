package com.graphics.ui;

/** Pantallas superpuestas de inicio, pausa y victoria. */
public class Menu {

    public void dibujarInicio(Hud hud, int totalEntregas) {
        oscurecer(hud, 0.55f);
        int t = hud.escala();
        float y = hud.alto() * 0.25f;
        hud.textoCentrado("CIUDAD CONDUCCION", y, t * 5, 1, 0.78f, 0.25f);
        y += hud.altoLinea(t * 5) + 10 * t;
        hud.textoCentrado("ENTREGA LOS " + totalEntregas + " PAQUETES EN LAS MARCAS DORADAS", y, t * 2, 1, 1, 1);
        y += hud.altoLinea(t * 2);
        hud.textoCentrado("FRENA SOBRE LA MARCA PARA ENTREGAR", y, t * 2, 1, 1, 1);
        y += hud.altoLinea(t * 2) + 14 * t;
        hud.textoCentrado("PRESIONA ENTER PARA COMENZAR", y, t * 3, 0.45f, 1, 0.55f);
        y += hud.altoLinea(t * 3) + 6 * t;
        hud.textoCentrado("H: CONTROLES   ESC: SALIR", y, t * 2, 0.62f, 0.72f, 0.82f);
        hud.textoCentrado("AUTOR: GABRIEL MAMANI SANDOVAL", hud.alto() - hud.altoLinea(t) * 2, t, 0.62f, 0.72f, 0.82f);
    }

    public void dibujarPausa(Hud hud) {
        oscurecer(hud, 0.5f);
        int t = hud.escala();
        float y = hud.alto() * 0.38f;
        hud.textoCentrado("PAUSA", y, t * 6, 1, 1, 1);
        y += hud.altoLinea(t * 6) + 8 * t;
        hud.textoCentrado("P: CONTINUAR   R: REINICIAR   ESC: SALIR", y, t * 2, 0.62f, 0.72f, 0.82f);
    }

    public void dibujarVictoria(Hud hud, int segundos) {
        int t = hud.escala();
        float alto = hud.altoLinea(t * 4) + hud.altoLinea(t * 2) + 20 * t;
        float y = hud.alto() * 0.3f;
        hud.rectangulo(0, y, hud.ancho(), alto, 0.05f, 0.08f, 0.12f, 0.8f);
        hud.textoCentrado("GANASTE EN " + segundos + " S", y + 8 * t, t * 4, 1, 0.78f, 0.25f);
        hud.textoCentrado("R: JUGAR OTRA VEZ", y + 12 * t + hud.altoLinea(t * 4), t * 2, 1, 1, 1);
    }

    private static void oscurecer(Hud hud, float opacidad) {
        hud.rectangulo(0, 0, hud.ancho(), hud.alto(), 0, 0, 0, opacidad);
    }
}
