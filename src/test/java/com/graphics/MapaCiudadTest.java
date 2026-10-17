package com.graphics;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import com.graphics.juego.Entregas;
import com.graphics.mundo.Colisiones;
import com.graphics.mundo.Manzana;
import com.graphics.mundo.MapaCiudad;
import com.graphics.mundo.Sector;
import com.graphics.mundo.TipoCelda;
import com.graphics.vehiculo.Auto;

import junit.framework.TestCase;

/** Ampliación de la ciudad: tamaño, distribución, escala derivada, accesos y colisiones. */
public class MapaCiudadTest extends TestCase {

    private final MapaCiudad mapa = new MapaCiudad();
    private final Colisiones colisiones = new Colisiones(mapa);

    public void testDimensionesYEscala() {
        assertTrue(mapa.filas() >= 11);
        assertTrue(mapa.columnas() >= 11);
        assertEquals(mapa.filas() * MapaCiudad.CELDA / 2, mapa.limite(), 0f);
        assertEquals(110f, mapa.tamano(), 0f);
        assertEquals(-50f, mapa.centro(0), 0f);
        assertEquals(50f, mapa.centro(10), 0f);
    }

    public void testDistribucionUrbana() {
        assertTrue(mapa.contar(TipoCelda.EDIFICIO) >= 12);
        assertTrue(mapa.contar(TipoCelda.PARQUE) >= 4);
        Set<Float> alturas = new HashSet<>();
        Set<Sector> sectores = EnumSet.noneOf(Sector.class);
        for (Manzana manzana : mapa.manzanas()) {
            if (manzana.esEdificio()) {
                alturas.add(manzana.altura());
            }
            sectores.add(manzana.sector());
        }
        assertTrue("Las alturas deben variar", alturas.size() >= 5);
        assertEquals(Sector.values().length, sectores.size());
    }

    /** Búsqueda en anchura por celdas de calle desde el inicio: todos los destinos deben alcanzarse. */
    public void testDestinosAccesiblesPorCalles() {
        boolean[][] visitada = new boolean[mapa.filas()][mapa.columnas()];
        Deque<int[]> pendientes = new ArrayDeque<>();
        int[] inicio = {mapa.indice(Auto.INICIO_Z), mapa.indice(Auto.INICIO_X)};
        assertTrue(mapa.esCalle(inicio[0], inicio[1]));
        pendientes.add(inicio);
        visitada[inicio[0]][inicio[1]] = true;
        int[][] vecinos = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!pendientes.isEmpty()) {
            int[] celda = pendientes.poll();
            for (int[] paso : vecinos) {
                int fila = celda[0] + paso[0];
                int columna = celda[1] + paso[1];
                if (mapa.esCalle(fila, columna) && !visitada[fila][columna]) {
                    visitada[fila][columna] = true;
                    pendientes.add(new int[] {fila, columna});
                }
            }
        }
        for (float[] destino : Entregas.DESTINOS) {
            int fila = mapa.indice(destino[1]);
            int columna = mapa.indice(destino[0]);
            assertTrue("Destino sin acceso: " + destino[0] + ", " + destino[1], visitada[fila][columna]);
            assertTrue(colisiones.puedeCircular(destino[0], destino[1], Auto.RADIO));
        }
    }

    public void testLimitesYObstaculos() {
        assertFalse(colisiones.puedeCircular(-40, -40, Auto.RADIO));          // centro de un edificio
        assertFalse(colisiones.puedeCircular(0, -40, Auto.RADIO));            // centro de un parque
        assertFalse(colisiones.puedeCircular(-46.5f, -40, Auto.RADIO));       // invade la acera
        assertTrue(colisiones.puedeCircular(-47, -40, Auto.RADIO));           // separado de la acera
        assertFalse(colisiones.puedeCircular(54, 0, Auto.RADIO));             // borde este
        assertFalse(colisiones.puedeCircular(0, -56, Auto.RADIO));            // fuera por el norte
        for (float z = -50; z <= 50; z += 0.5f) {
            assertTrue(colisiones.puedeCircular(-50, z, Auto.RADIO));         // avenida oeste completa
            assertTrue(colisiones.puedeCircular(z, 30, Auto.RADIO));          // avenida nueva al sur
        }
    }
}
