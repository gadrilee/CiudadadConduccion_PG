package com.graphics.render;

import com.graphics.mundo.Manzana;
import com.graphics.mundo.MapaCiudad;

/** Dibuja la estructura de la ciudad: suelo, aceras, edificios, parques y marcas viales. */
public class DibujoCiudad {

    private final Renderizador render;
    private final MapaCiudad mapa;

    public DibujoCiudad(Renderizador render, MapaCiudad mapa) {
        this.render = render;
        this.mapa = mapa;
    }

    public void dibujar() {
        float tamano = mapa.tamano();
        // Asfalto: su cara superior queda en Y = 0 y cubre todo el mapa.
        render.caja(0, -0.25f, 0, tamano, 0.5f, tamano, 0.16f, 0.19f, 0.23f);
        for (Manzana manzana : mapa.manzanas()) {
            dibujarManzana(manzana);
        }
        dibujarMarcasCalle();
    }

    private void dibujarManzana(Manzana m) {
        render.caja(m.x(), 0.15f, m.z(), MapaCiudad.CELDA, 0.3f, MapaCiudad.CELDA, 0.60f, 0.64f, 0.66f);
        if (m.esEdificio()) {
            float altura = m.altura();
            render.caja(m.x(), altura / 2 + 0.3f, m.z(), 7, altura, 7, m.rojo(), m.verde(), m.azul());
            if (!render.modoMapa()) {
                // Desde arriba el techo oscuro ocultaría el color del edificio en el minimapa.
                render.caja(m.x(), altura + 0.45f, m.z(), 7.3f, 0.3f, 7.3f, 0.20f, 0.26f, 0.32f);
            }
        } else {
            render.caja(m.x(), 0.32f, m.z(), 9, 0.1f, 9, 0.20f, 0.45f, 0.28f);
        }
    }

    /** Líneas amarillas discontinuas en los tramos entre cruces; deja libres los extremos para los pasos peatonales. */
    private void dibujarMarcasCalle() {
        if (render.modoMapa()) {
            return;
        }
        for (int fila = 0; fila < mapa.filas(); fila++) {
            for (int columna = 0; columna < mapa.columnas(); columna++) {
                if (!mapa.esCalle(fila, columna)) {
                    continue;
                }
                float x = mapa.centro(columna);
                float z = mapa.centro(fila);
                boolean tramoHorizontal = fila % 2 == 0 && columna % 2 == 1;
                boolean tramoVertical = columna % 2 == 0 && fila % 2 == 1;
                for (float desplazamiento = -1.5f; desplazamiento <= 1.5f; desplazamiento += 3) {
                    if (tramoHorizontal) {
                        render.caja(x + desplazamiento, 0.025f, z, 1.6f, 0.03f, 0.13f, 1, 0.84f, 0.35f);
                    }
                    if (tramoVertical) {
                        render.caja(x, 0.025f, z + desplazamiento, 0.13f, 0.03f, 1.6f, 1, 0.84f, 0.35f);
                    }
                }
            }
        }
    }
}
