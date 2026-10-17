package com.graphics.ui;

import com.graphics.render.MallaQuad;
import com.graphics.render.ProgramaShader;

import static org.lwjgl.opengl.GL33.*;

/**
 * Interfaz 2D superpuesta: rectángulos semitransparentes y texto con {@link FuentePixel}.
 * Se dibuja al final del cuadro, sin prueba de profundidad y con mezcla alfa.
 */
public class Hud {

    private static final float VELOCIDAD_BARRA_MAXIMA = 60;

    private final ProgramaShader programa;
    private final MallaQuad quad;
    private int ancho;
    private int alto;
    private int escala;

    public Hud() {
        programa = new ProgramaShader("/shaders/hud.vert", "/shaders/hud.frag");
        quad = new MallaQuad();
    }

    /** Prepara el pase 2D; la escala del texto crece con el tamaño de la ventana. */
    public void comenzar(int ancho, int alto) {
        this.ancho = ancho;
        this.alto = alto;
        escala = Math.max(1, Math.min(ancho, alto) / 380);
        glViewport(0, 0, ancho, alto);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        programa.usar();
        programa.vector2("uPantalla", ancho, alto);
        quad.enlazar();
    }

    /** Restaura el estado que espera el pase 3D del siguiente cuadro. */
    public void terminar() {
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }

    public void eliminar() {
        quad.eliminar();
        programa.eliminar();
    }

    public int ancho() {
        return ancho;
    }

    public int alto() {
        return alto;
    }

    public int escala() {
        return escala;
    }

    /** Rectángulo en píxeles con origen en la esquina superior izquierda. */
    public void rectangulo(float x, float y, float w, float h, float r, float g, float b, float a) {
        programa.vector4("uRect", x, y, w, h);
        programa.vector4("uColor", r, g, b, a);
        quad.dibujar();
    }

    /** Escribe texto; cada píxel de la fuente mide {@code tamano} píxeles de pantalla. */
    public void texto(String texto, float x, float y, int tamano, float r, float g, float b) {
        String normalizado = FuentePixel.normalizar(texto);
        programa.vector4("uColor", r, g, b, 1);
        for (int i = 0; i < normalizado.length(); i++) {
            char caracter = normalizado.charAt(i);
            float origenX = x + i * FuentePixel.AVANCE * tamano;
            for (int fila = 0; fila < FuentePixel.ALTO; fila++) {
                // Une los píxeles encendidos consecutivos en un solo rectángulo.
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
                    programa.vector4("uRect", origenX + inicio * tamano, y + fila * tamano,
                        (columna - inicio) * tamano, tamano);
                    quad.dibujar();
                }
            }
        }
    }

    public int anchoTexto(String texto, int tamano) {
        return FuentePixel.ancho(FuentePixel.normalizar(texto)) * tamano;
    }

    public int altoLinea(int tamano) {
        return (FuentePixel.ALTO + 4) * tamano;
    }

    /** Texto centrado horizontalmente; si no cabe en la ventana se reduce su tamaño. */
    public void textoCentrado(String texto, float y, int tamano, float r, float g, float b) {
        int ajustado = tamano;
        while (ajustado > 1 && anchoTexto(texto, ajustado) > ancho * 0.94f) {
            ajustado--;
        }
        texto(texto, (ancho - anchoTexto(texto, ajustado)) / 2f, y, ajustado, r, g, b);
    }

    /** Panel de estado (arriba a la izquierda): velocidad, luces, cámara, entregas, tiempo y sector. */
    public void dibujarPanel(DatosHud datos) {
        int t = escala;
        int margen = 8 * t;
        int linea = altoLinea(t);
        String[][] filas = {
            {"VELOCIDAD", datos.kilometrosPorHora() + " KM/H"},
            {"FAROS", datos.faros() ? "ENCENDIDOS" : "APAGADOS"},
            {"AMBIENTE", datos.noche() ? "NOCHE" : "DIA"},
            {"CAMARA", datos.camara()},
            {"ENTREGAS", datos.entregas() + "/" + datos.totalEntregas()},
            {"TIEMPO", datos.segundos() + " S"},
            {"SECTOR", datos.sector()},
            {"CLAXON", datos.claxon() ? "ON" : "OFF"},
            {"MINIMAPA", datos.minimapa() ? "VISIBLE" : "OCULTO"}
        };
        int columnaValor = 11 * FuentePixel.AVANCE * t;
        int anchoPanel = columnaValor + 17 * FuentePixel.AVANCE * t + margen * 2;
        int altoPanel = margen * 2 + linea * (filas.length + 2) + 6 * t;
        rectangulo(margen, margen, anchoPanel, altoPanel, 0.04f, 0.07f, 0.11f, 0.78f);

        float x = margen * 2;
        float y = margen * 2;
        texto("CIUDAD CONDUCCION", x, y, t, 1, 0.78f, 0.25f);
        y += linea;
        float proporcion = Math.min(1, datos.kilometrosPorHora() / VELOCIDAD_BARRA_MAXIMA);
        float anchoBarra = anchoPanel - margen * 2;
        rectangulo(x, y, anchoBarra, 4 * t, 0.2f, 0.25f, 0.3f, 1);
        rectangulo(x, y, anchoBarra * proporcion, 4 * t, 0.2f + proporcion * 0.8f, 0.9f - proporcion * 0.6f, 0.3f, 1);
        y += 6 * t + 4 * t;
        for (String[] fila : filas) {
            texto(fila[0], x, y, t, 0.62f, 0.72f, 0.82f);
            texto(fila[1], x + columnaValor, y, t, 1, 1, 1);
            y += linea;
        }
        texto("H: AYUDA   P: PAUSA", x, y, t, 0.62f, 0.72f, 0.82f);
    }

    /** Lista de controles (abajo a la izquierda). */
    public void dibujarAyuda() {
        String[] lineas = {
            "W/S  ACELERAR / RETROCEDER",
            "A/D  GIRAR",
            "ESPACIO  FRENO",
            "Q  CLAXON",
            "C  CAMARA",
            "N  DIA / NOCHE",
            "F  FAROS",
            "M  MINIMAPA",
            "R  REINICIAR",
            "P  PAUSA",
            "H  OCULTAR AYUDA",
            "ESC  SALIR"
        };
        int t = escala;
        int margen = 8 * t;
        int linea = altoLinea(t);
        int anchoPanel = 28 * FuentePixel.AVANCE * t + margen * 2;
        int altoPanel = linea * (lineas.length + 1) + margen * 2;
        float x = margen;
        float y = alto - altoPanel - margen;
        rectangulo(x, y, anchoPanel, altoPanel, 0.04f, 0.07f, 0.11f, 0.78f);
        texto("CONTROLES", x + margen, y + margen, t, 1, 0.78f, 0.25f);
        for (int i = 0; i < lineas.length; i++) {
            texto(lineas[i], x + margen, y + margen + linea * (i + 1), t, 1, 1, 1);
        }
    }

    /** Mensaje breve centrado en la parte superior (por ejemplo, al completar una entrega). */
    public void dibujarAviso(String mensaje) {
        int t = escala * 2;
        int ancho = anchoTexto(mensaje, t) + 16 * escala;
        int altoCaja = altoLinea(t) + 8 * escala;
        float x = (this.ancho - ancho) / 2f;
        float y = 16 * escala;
        rectangulo(x, y, ancho, altoCaja, 0.05f, 0.30f, 0.12f, 0.85f);
        texto(mensaje, x + 8 * escala, y + 6 * escala, t, 1, 1, 1);
    }
}
