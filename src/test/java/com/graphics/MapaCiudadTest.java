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
            assertTrue(colisiones.puedeUbicar(Auto.forma(destino[0], destino[1], 0)));
        }
    }

    public void testLimitesYObstaculos() {
        float norte = 0;
        float oeste = (float) (Math.PI / 2);
        assertFalse(colisiones.puedeUbicar(Auto.forma(-40, -40, norte)));      // centro de un edificio
        assertFalse(colisiones.puedeUbicar(Auto.forma(0, -40, norte)));        // centro de un parque
        assertFalse(colisiones.puedeUbicar(Auto.forma(-45.5f, -40, norte)));   // invade la acera
        assertFalse(colisiones.puedeUbicar(Auto.forma(54.5f, 0, norte)));      // borde este
        assertFalse(colisiones.puedeUbicar(Auto.forma(0, -56, norte)));        // fuera por el norte
        for (float z = -50; z <= 50; z += 0.5f) {
            assertTrue(colisiones.puedeUbicar(Auto.forma(-50, z, norte)));     // avenida oeste completa
            assertTrue(colisiones.puedeUbicar(Auto.forma(z, 30, oeste)));      // avenida nueva al sur
        }
    }

    /** La forma es el rectángulo real del auto: puede ir pegado a la acera, pero no atravesada. */
    public void testElAutoPuedeIrPegadoALaAcera() {
        float pegado = -45 - Auto.MEDIO_ANCHO - 0.02f;
        assertTrue(colisiones.puedeUbicar(Auto.forma(pegado, -40, 0)));
        assertFalse(colisiones.puedeUbicar(Auto.forma(pegado, -40, (float) (Math.PI / 2))));
    }
}
