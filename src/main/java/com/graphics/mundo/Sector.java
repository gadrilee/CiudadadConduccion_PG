package com.graphics.mundo;

/**
 * Zonas con nombre de la ciudad. Cada sector tiene su propio estilo de edificios
 * y se muestra en el HUD y en carteles junto a las manzanas.
 */
public enum Sector {
    CENTRO("CENTRO", "DISTRITO CENTRO", 0.42f, 0.47f, 0.60f),
    NORTE("NORTE", "BARRIO NORTE", 0.58f, 0.40f, 0.34f),
    SUR("SUR", "PUERTO SUR", 0.62f, 0.57f, 0.44f),
    ESTE("ESTE", "VILLA ESTE", 0.38f, 0.53f, 0.48f),
    OESTE("OESTE", "LOMAS OESTE", 0.57f, 0.44f, 0.56f);

    private final String etiqueta;
    private final String nombre;
    private final float rojo;
    private final float verde;
    private final float azul;

    Sector(String etiqueta, String nombre, float rojo, float verde, float azul) {
        this.etiqueta = etiqueta;
        this.nombre = nombre;
        this.rojo = rojo;
        this.verde = verde;
        this.azul = azul;
    }

    /** Texto corto para los carteles. */
    public String etiqueta() {
        return etiqueta;
    }

    /** Nombre completo para el HUD. */
    public String nombre() {
        return nombre;
    }

    public float rojo() {
        return rojo;
    }

    public float verde() {
        return verde;
    }

    public float azul() {
        return azul;
    }

    /**
     * Las celdas a dos o menos del centro forman el CENTRO; el resto se reparte según
     * el eje en que más se alejan del centro (norte = filas pequeñas, es decir -Z).
     */
    public static Sector de(int fila, int columna, int filas, int columnas) {
        int desplazamientoFila = fila - filas / 2;
        int desplazamientoColumna = columna - columnas / 2;
        if (Math.abs(desplazamientoFila) <= 2 && Math.abs(desplazamientoColumna) <= 2) {
            return CENTRO;
        }
        if (Math.abs(desplazamientoFila) >= Math.abs(desplazamientoColumna)) {
            return desplazamientoFila < 0 ? NORTE : SUR;
        }
        return desplazamientoColumna < 0 ? OESTE : ESTE;
    }
}
