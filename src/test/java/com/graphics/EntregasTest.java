package com.graphics;

import com.graphics.juego.Entregas;
import com.graphics.mundo.MapaCiudad;

import junit.framework.TestCase;

/** Reglas de las entregas y reinicio del recorrido. */
public class EntregasTest extends TestCase {

    public void testDestinosSobreCalles() {
        MapaCiudad mapa = new MapaCiudad();
        for (float[] destino : Entregas.DESTINOS) {
            assertTrue(mapa.esCalleEn(destino[0], destino[1]));
        }
    }

    public void testHayQueFrenarCercaDelDestino() {
        Entregas entregas = new Entregas();
        float[] destino = entregas.destinoActual();
        assertFalse(entregas.actualizar(0.1f, destino[0] + 10, destino[1], 0));
        assertFalse(entregas.actualizar(0.1f, destino[0] + 1, destino[1], 5));
        assertTrue(entregas.actualizar(0.1f, destino[0] + 1, destino[1], 0.5f));
        assertEquals(1, entregas.completadas());
    }

    public void testCompletarYReiniciar() {
        Entregas entregas = new Entregas();
        while (!entregas.completo()) {
            float[] destino = entregas.destinoActual();
            entregas.actualizar(1, destino[0], destino[1], 0);
        }
        assertNull(entregas.destinoActual());
        float tiempoFinal = entregas.tiempo();
        assertFalse(entregas.actualizar(1, 0, 0, 0));
        assertEquals(tiempoFinal, entregas.tiempo(), 0f);

        entregas.reiniciar();
        assertEquals(0, entregas.completadas());
        assertEquals(0f, entregas.tiempo(), 0f);
        assertNotNull(entregas.destinoActual());
    }
}
