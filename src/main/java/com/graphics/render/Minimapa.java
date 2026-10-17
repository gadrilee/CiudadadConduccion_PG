package com.graphics.render;

import java.util.List;

import com.graphics.trafico.VehiculoAutonomo;
import com.graphics.vehiculo.Auto;
import com.graphics.vehiculo.Chata;

import static org.lwjgl.opengl.GL33.*;

/**
 * Segundo pase de dibujo: la ciudad vista desde arriba (norte arriba) en un recuadro de la esquina
 * superior derecha. Usa scissor para borrar solo su recuadro y restaura todo al terminar.
 */
public class Minimapa {

    private static final int LADO_MAXIMO = 260;

    private final Renderizador render;

    public Minimapa(Renderizador render) {
        this.render = render;
    }

    /** Lado del recuadro en píxeles: como mucho 260 y un tercio de la dimensión menor de la ventana. */
    public static int lado(int ancho, int alto) {
        return Math.min(LADO_MAXIMO, Math.min(ancho, alto) / 3);
    }

    /**
     * @param escena dibuja la ciudad; se ejecuta con {@link Renderizador#modoMapa()} activo
     */
    public void dibujar(int ancho, int alto, float mitadMapa, Runnable escena, Auto auto,
                        float[] destino, List<VehiculoAutonomo> trafico) {
        int dimensionMenor = Math.min(ancho, alto);
        int lado = lado(ancho, alto);
        int margen = Math.min(18, dimensionMenor / 20);
        int x = ancho - lado - margen;
        int y = alto - lado - margen; // OpenGL mide Y desde abajo: esto lo deja arriba.

        glEnable(GL_SCISSOR_TEST);
        try {
            glScissor(x - 3, y - 3, lado + 6, lado + 6);
            glClearColor(0.8f, 0.87f, 0.94f, 1);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glScissor(x, y, lado, lado);
            glClearColor(0.06f, 0.10f, 0.15f, 1);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glViewport(x, y, lado, lado);

            render.programa().decimal("uMitadMapa", mitadMapa);
            render.setModoMapa(true);
            escena.run();
            dibujarIndicadores(auto, destino, trafico);
        } finally {
            render.setModoMapa(false);
            glDisable(GL_SCISSOR_TEST);
            glViewport(0, 0, ancho, alto);
        }
    }

    /** Marcas por encima de los edificios (Y alta = delante en la proyección del mapa). */
    private void dibujarIndicadores(Auto auto, float[] destino, List<VehiculoAutonomo> trafico) {
        if (destino != null) {
            render.caja(destino[0], 23, destino[1], 7, 0.1f, 7, 1, 0.72f, 0.12f);
        }
        for (VehiculoAutonomo vehiculo : trafico) {
            render.cajaGirada(vehiculo.x(), 24, vehiculo.z(), 2.0f, 0.1f, 3.0f, 0.92f, 0.92f, 0.92f, vehiculo.angulo());
        }
        Chata chata = auto.chata();
        if (chata != null) {
            render.cajaGirada(chata.x(), 24.5f, chata.z(), 2.0f, 0.1f, 2.6f, 0.05f, 0.6f, 0.6f, chata.angulo());
        }
        render.cajaGirada(auto.x(), 25, auto.z(), 2.6f, 0.1f, 3.6f, 0.1f, 1, 1, auto.angulo());
        float puntaX = auto.x() + auto.frenteX() * 2.4f;
        float puntaZ = auto.z() + auto.frenteZ() * 2.4f;
        render.caja(puntaX, 26, puntaZ, 1.2f, 0.1f, 1.2f, 1, 1, 1);
    }
}
