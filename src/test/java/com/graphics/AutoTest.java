package com.graphics;

import com.graphics.mundo.Colisiones;
import com.graphics.mundo.MapaCiudad;
import com.graphics.vehiculo.Auto;
import com.graphics.vehiculo.ControlesAuto;

import junit.framework.TestCase;

/** Conducción: reinicio, independencia de la frecuencia de cuadros y colisiones. */
public class AutoTest extends TestCase {

    private static final ControlesAuto ACELERAR = new ControlesAuto(1, 0, false);

    private final MapaCiudad mapa = new MapaCiudad();
    private final Colisiones colisiones = new Colisiones(mapa);

    public void testReinicioRestauraTodo() {
        Auto auto = new Auto();
        auto.colocar(12, 4, 2, 10);
        auto.reiniciar();
        assertEquals(Auto.INICIO_X, auto.x(), 0f);
        assertEquals(Auto.INICIO_Z, auto.z(), 0f);
        assertEquals(0f, auto.velocidad(), 0f);
        assertEquals(0f, auto.angulo(), 0f);
    }

    /** Un segundo de aceleración a 30 y a 120 cuadros por segundo debe recorrer casi lo mismo. */
    public void testMovimientoIndependienteDeLosFps() {
        Auto lento = simular(30, 1);
        Auto rapido = simular(120, 1);
        assertTrue(lento.z() < Auto.INICIO_Z - 2);
        assertEquals(lento.z(), rapido.z(), 0.3f);
        assertEquals(lento.velocidad(), rapido.velocidad(), 0.1f);
    }

    public void testElBordeDetieneAlAuto() {
        Auto auto = simular(60, 20);
        assertTrue(auto.z() >= -mapa.limite() + Auto.RADIO);
        assertTrue(colisiones.puedeCircular(auto.x(), auto.z(), Auto.RADIO));
    }

    public void testUnEdificioDetieneAlAuto() {
        Auto auto = new Auto();
        // Calle oeste a la altura de la primera fila de manzanas, mirando al este (hacia un edificio).
        auto.colocar(-50, -40, (float) (-Math.PI / 2), 0);
        for (int i = 0; i < 600; i++) {
            auto.actualizar(1 / 60f, ACELERAR, colisiones);
        }
        assertTrue(auto.x() < -45 - Auto.RADIO + 0.01f);
        assertTrue(colisiones.puedeCircular(auto.x(), auto.z(), Auto.RADIO));
    }

    private Auto simular(int fps, float segundos) {
        Auto auto = new Auto();
        int pasos = Math.round(fps * segundos);
        for (int i = 0; i < pasos; i++) {
            auto.actualizar(1f / fps, ACELERAR, colisiones);
        }
        return auto;
    }
}
