package com.graphics;

import java.util.EnumSet;
import java.util.Set;

import com.graphics.iluminacion.Farola;
import com.graphics.iluminacion.Iluminacion;
import com.graphics.iluminacion.Semaforo;
import com.graphics.mundo.MapaCiudad;
import com.graphics.mundo.Sector;

import junit.framework.TestCase;

/** Farolas, interruptores N/F y secuencia de los semáforos. */
public class IluminacionTest extends TestCase {

    private final MapaCiudad mapa = new MapaCiudad();

    public void testFarolasRepartidasPorLaCiudad() {
        Iluminacion iluminacion = new Iluminacion(mapa);
        int cantidad = iluminacion.farolas().size();
        assertTrue(cantidad >= 9);
        assertTrue(cantidad <= Iluminacion.MAX_FAROLAS);
        Set<Sector> sectores = EnumSet.noneOf(Sector.class);
        for (Farola farola : iluminacion.farolas()) {
            assertFalse("La farola debe estar en la acera", mapa.esCalleEn(farola.x(), farola.z()));
            sectores.add(mapa.sectorEn(farola.x(), farola.z()));
        }
        assertTrue(sectores.size() >= 4);
    }

    public void testInterruptores() {
        Iluminacion iluminacion = new Iluminacion(mapa);
        boolean noche = iluminacion.esNoche();
        boolean faros = iluminacion.farosEncendidos();
        iluminacion.alternarNoche();
        iluminacion.alternarFaros();
        assertEquals(!noche, iluminacion.esNoche());
        assertEquals(!faros, iluminacion.farosEncendidos());
    }

    public void testSecuenciaRojoVerdeAmarillo() {
        assertEquals(Semaforo.Fase.ROJO, Semaforo.faseEn(0));
        assertEquals(Semaforo.Fase.ROJO, Semaforo.faseEn(4.9f));
        assertEquals(Semaforo.Fase.VERDE, Semaforo.faseEn(5));
        assertEquals(Semaforo.Fase.AMARILLO, Semaforo.faseEn(10.5f));
        assertEquals(Semaforo.Fase.ROJO, Semaforo.faseEn(Semaforo.CICLO + 1));
    }

    public void testSemaforosDistribuidos() {
        assertFalse(Semaforo.distribuir(mapa).isEmpty());
        Semaforo desfasado = new Semaforo(0, 0, Semaforo.CICLO / 2);
        assertEquals(Semaforo.Fase.VERDE, desfasado.fase(0));
    }
}
