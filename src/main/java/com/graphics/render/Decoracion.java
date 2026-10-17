package com.graphics.render;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.graphics.iluminacion.Semaforo;
import com.graphics.mundo.Manzana;
import com.graphics.mundo.MapaCiudad;
import com.graphics.mundo.Sector;
import com.graphics.ui.FuentePixel;

/**
 * Detalles urbanos de la vista principal: árboles y bancos, ventanas, pasos peatonales,
 * semáforos, carteles de sector y mobiliario (papeleras e hidrantes).
 * Esquinas de cada manzana: NO = farola, NE = semáforo, SE = papelera, borde sur = cartel.
 */
public class Decoracion {

    private static final float ESQUINA = 4.2f;
    private static final float TAMANO_PIXEL_CARTEL = 0.14f;

    private final Renderizador render;
    private final MapaCiudad mapa;
    private final Map<Sector, Manzana> carteles = new EnumMap<>(Sector.class);

    public Decoracion(Renderizador render, MapaCiudad mapa) {
        this.render = render;
        this.mapa = mapa;
        for (Manzana manzana : mapa.manzanas()) {
            carteles.putIfAbsent(manzana.sector(), manzana);
        }
    }

    public void dibujar(boolean noche, float tiempo, List<Semaforo> semaforos) {
        for (Manzana manzana : mapa.manzanas()) {
            if (manzana.esParque()) {
                dibujarParque(manzana.x(), manzana.z());
            } else {
                dibujarVentanas(manzana, noche);
                dibujarHidrante(manzana.x() + ESQUINA, manzana.z() + 1.5f);
            }
            dibujarPasosPeatonales(manzana.x(), manzana.z());
            dibujarPapelera(manzana.x() + ESQUINA, manzana.z() + ESQUINA);
        }
        for (Semaforo semaforo : semaforos) {
            dibujarSemaforo(semaforo, tiempo);
        }
        for (Map.Entry<Sector, Manzana> cartel : carteles.entrySet()) {
            Manzana manzana = cartel.getValue();
            dibujarCartel(cartel.getKey().etiqueta(), manzana.x() - 4.6f, manzana.z() + 4.4f, noche);
        }
    }

    /** Cuatro árboles (tronco + copa) y un banco con respaldo. */
    private void dibujarParque(float x, float z) {
        float[] posiciones = {-2.5f, 2.5f};
        for (float desplazamientoX : posiciones) {
            for (float desplazamientoZ : posiciones) {
                float arbolX = x + desplazamientoX;
                float arbolZ = z + desplazamientoZ;
                render.caja(arbolX, 1.2f, arbolZ, 0.35f, 2, 0.35f, 0.38f, 0.22f, 0.12f);
                render.caja(arbolX, 2.7f, arbolZ, 2, 2.3f, 2, 0.12f, 0.42f, 0.23f);
            }
        }
        render.caja(x, 0.65f, z, 3, 0.25f, 0.8f, 0.55f, 0.30f, 0.13f);
        render.caja(x, 0.4f, z, 2, 0.6f, 0.35f, 0.22f, 0.24f, 0.24f);
        render.caja(x, 1, z + 0.35f, 3, 0.7f, 0.15f, 0.55f, 0.30f, 0.13f);
    }

    /**
     * Tres ventanas por piso en cada fachada. De noche dos de cada tres se encienden (emisión);
     * el patrón depende de la posición para que no todas las fachadas sean iguales.
     */
    private void dibujarVentanas(Manzana m, boolean noche) {
        float x = m.x();
        float z = m.z();
        int piso = 0;
        for (float y = 1.7f; y < m.altura(); y += 2, piso++) {
            int ventana = 0;
            for (float d = -2; d <= 2; d += 2, ventana++) {
                boolean encendida = noche && (m.fila() + m.columna() + piso + ventana) % 3 != 0;
                float rojo;
                float verde;
                float azul;
                if (encendida) {
                    rojo = 0.95f;
                    verde = 0.78f;
                    azul = 0.40f;
                } else if (noche) {
                    rojo = 0.10f;
                    verde = 0.12f;
                    azul = 0.17f;
                } else {
                    rojo = 0.55f;
                    verde = 0.70f;
                    azul = 0.82f;
                }
                render.emision(encendida);
                render.caja(x + d, y, z - 3.51f, 0.8f, 0.9f, 0.04f, rojo, verde, azul);
                render.caja(x + d, y, z + 3.51f, 0.8f, 0.9f, 0.04f, rojo, verde, azul);
                render.caja(x - 3.51f, y, z + d, 0.04f, 0.9f, 0.8f, rojo, verde, azul);
                render.caja(x + 3.51f, y, z + d, 0.04f, 0.9f, 0.8f, rojo, verde, azul);
            }
        }
        render.emision(false);
    }

    /**
     * Cebras en la calle norte y en la calle oeste de la manzana, junto al cruce.
     * Las franjas son paralelas al tráfico y se repiten a lo ancho de la calzada.
     */
    private void dibujarPasosPeatonales(float x, float z) {
        for (float desplazamiento = -14; desplazamiento <= -6; desplazamiento += 1) {
            render.caja(x - 4, 0.045f, z + desplazamiento, 1.8f, 0.04f, 0.5f, 0.85f, 0.87f, 0.83f);
            render.caja(x + desplazamiento, 0.045f, z - 4, 0.5f, 0.04f, 1.8f, 0.85f, 0.87f, 0.83f);
        }
    }

    /** Poste, carcasa y tres bombillas (rojo arriba, amarillo, verde abajo) visibles desde el norte y el sur. */
    private void dibujarSemaforo(Semaforo semaforo, float tiempo) {
        float x = semaforo.x();
        float z = semaforo.z();
        render.caja(x, 1.7f, z, 0.18f, 2.8f, 0.18f, 0.18f, 0.20f, 0.22f);
        render.caja(x, 3.1f, z, 0.55f, 1.2f, 0.45f, 0.08f, 0.10f, 0.12f);
        Semaforo.Fase activa = semaforo.fase(tiempo);
        Semaforo.Fase[] orden = {Semaforo.Fase.ROJO, Semaforo.Fase.AMARILLO, Semaforo.Fase.VERDE};
        for (int i = 0; i < orden.length; i++) {
            boolean encendida = orden[i] == activa;
            float brillo = encendida ? 1 : 0.15f;
            float rojo = (orden[i] != Semaforo.Fase.VERDE) ? brillo : 0;
            float verde = (orden[i] != Semaforo.Fase.ROJO) ? brillo : 0;
            float altura = 3.45f - i * 0.35f;
            render.emision(encendida);
            render.caja(x, altura, z - 0.24f, 0.28f, 0.25f, 0.06f, rojo, verde, 0.02f);
            render.caja(x, altura, z + 0.24f, 0.28f, 0.25f, 0.06f, rojo, verde, 0.02f);
        }
        render.emision(false);
    }

    /** Cartel verde con el nombre del sector escrito con la fuente pixelada (cada píxel es una caja). */
    private void dibujarCartel(String texto, float izquierda, float z, boolean noche) {
        float p = TAMANO_PIXEL_CARTEL;
        float ancho = (FuentePixel.ancho(texto) + 2) * p;
        float alto = (FuentePixel.ALTO + 2) * p;
        float centroX = izquierda + ancho / 2;
        float centroY = 3.0f;
        render.caja(izquierda + 0.1f, 1.5f, z, 0.12f, 3.0f, 0.12f, 0.25f, 0.27f, 0.30f);
        render.caja(izquierda + ancho - 0.1f, 1.5f, z, 0.12f, 3.0f, 0.12f, 0.25f, 0.27f, 0.30f);
        render.caja(centroX, centroY, z, ancho, alto, 0.1f, 0.10f, 0.40f, 0.22f);

        float arriba = centroY + alto / 2;
        float caraFrontal = z + 0.06f;
        render.emision(noche);
        for (int i = 0; i < texto.length(); i++) {
            char caracter = texto.charAt(i);
            for (int fila = 0; fila < FuentePixel.ALTO; fila++) {
                // Une los píxeles consecutivos de la fila en una sola caja para dibujar menos.
                int columna = 0;
                while (columna < FuentePixel.ANCHO) {
                    if (!FuentePixel.pixel(caracter, fila, columna)) {
                        columna++;
                        continue;
                    }
                    int inicio = columna;
                    while (columna < FuentePixel.ANCHO && FuentePixel.pixel(caracter, fila, columna)) {
                        columna++;
                    }
                    int largo = columna - inicio;
                    float px = izquierda + p * (1 + i * FuentePixel.AVANCE + inicio + largo / 2f);
                    float py = arriba - p * (1 + fila + 0.5f);
                    render.caja(px, py, caraFrontal, p * largo, p, 0.03f, 0.95f, 0.95f, 0.92f);
                }
            }
        }
        render.emision(false);
    }

    private void dibujarPapelera(float x, float z) {
        render.caja(x, 0.75f, z, 0.45f, 0.9f, 0.45f, 0.16f, 0.36f, 0.22f);
        render.caja(x, 1.22f, z, 0.52f, 0.06f, 0.52f, 0.12f, 0.14f, 0.15f);
    }

    private void dibujarHidrante(float x, float z) {
        render.caja(x, 0.65f, z, 0.32f, 0.7f, 0.32f, 0.80f, 0.12f, 0.10f);
        render.caja(x, 0.65f, z, 0.55f, 0.14f, 0.14f, 0.70f, 0.10f, 0.08f);
        render.caja(x, 1.05f, z, 0.22f, 0.12f, 0.22f, 0.65f, 0.65f, 0.62f);
    }
}
