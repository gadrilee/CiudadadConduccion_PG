package com.graphics.trafico;

import com.graphics.mundo.MapaCiudad;

/**
 * Recorrido cerrado por cruces de calles, con tramos solo horizontales o verticales.
 * Los puntos se desplazan al carril derecho para que los autos que van en sentidos
 * opuestos por la misma calle no choquen.
 */
public class Ruta {

    /** Distancia del eje de la calle al centro del carril (la calle mide 10 de ancho). */
    public static final float CARRIL = 2.5f;

    private final float[][] puntos;

    /**
     * @param cruces pares {fila, columna} de cruces (índices pares), en orden de recorrido
     */
    public Ruta(MapaCiudad mapa, int[][] cruces) {
        int cantidad = cruces.length;
        if (cantidad < 3) {
            throw new IllegalArgumentException("Una ruta necesita al menos tres cruces");
        }
        for (int i = 0; i < cantidad; i++) {
            int[] actual = cruces[i];
            int[] siguiente = cruces[(i + 1) % cantidad];
            if (!mapa.esCalle(actual[0], actual[1]) || actual[0] % 2 != 0 || actual[1] % 2 != 0) {
                throw new IllegalArgumentException("El punto " + i + " no es un cruce de calles");
            }
            boolean mismaFila = actual[0] == siguiente[0];
            boolean mismaColumna = actual[1] == siguiente[1];
            if (mismaFila == mismaColumna) {
                throw new IllegalArgumentException("El tramo " + i + " debe ser horizontal o vertical");
            }
        }
        puntos = new float[cantidad][];
        for (int i = 0; i < cantidad; i++) {
            puntos[i] = desplazarAlCarril(mapa, cruces[(i + cantidad - 1) % cantidad], cruces[i], cruces[(i + 1) % cantidad]);
        }
    }

    /**
     * La derecha de una dirección (dx, dz) es (-dz, dx); con dirección norte (0, -1) da el este (1, 0).
     * En una esquina se suman las derechas del tramo que llega y del que sale: así el punto queda
     * en el cruce de ambos carriles. Si los tramos siguen recto, basta con una.
     */
    private static float[] desplazarAlCarril(MapaCiudad mapa, int[] anterior, int[] actual, int[] siguiente) {
        float x = mapa.centro(actual[1]);
        float z = mapa.centro(actual[0]);
        float[] llegada = direccion(anterior, actual);
        float[] salida = direccion(actual, siguiente);
        float derechaLlegadaX = -llegada[1];
        float derechaLlegadaZ = llegada[0];
        float derechaSalidaX = -salida[1];
        float derechaSalidaZ = salida[0];
        boolean recto = llegada[0] == salida[0] && llegada[1] == salida[1];
        if (recto) {
            return new float[] {x + derechaLlegadaX * CARRIL, z + derechaLlegadaZ * CARRIL};
        }
        return new float[] {
            x + (derechaLlegadaX + derechaSalidaX) * CARRIL,
            z + (derechaLlegadaZ + derechaSalidaZ) * CARRIL
        };
    }

    /** Dirección unitaria en el mundo (X, Z) para ir de un cruce a otro. */
    private static float[] direccion(int[] desde, int[] hacia) {
        return new float[] {Math.signum(hacia[1] - desde[1]), Math.signum(hacia[0] - desde[0])};
    }

    public int cantidad() {
        return puntos.length;
    }

    /** Coordenada X del punto {@code indice}; el índice da la vuelta al final de la ruta. */
    public float x(int indice) {
        return puntos[Math.floorMod(indice, puntos.length)][0];
    }

    public float z(int indice) {
        return puntos[Math.floorMod(indice, puntos.length)][1];
    }
}
