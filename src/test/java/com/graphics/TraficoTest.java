package com.graphics;

import com.graphics.mundo.Colisiones;
import com.graphics.mundo.MapaCiudad;
import com.graphics.trafico.Ruta;
import com.graphics.trafico.Trafico;
import com.graphics.trafico.VehiculoAutonomo;
import com.graphics.vehiculo.Auto;

import junit.framework.TestCase;

/** Tráfico autónomo: cantidad, recorrido por calles con giros y bloqueo del jugador. */
public class TraficoTest extends TestCase {

    private final MapaCiudad mapa = new MapaCiudad();
    private final Colisiones colisiones = new Colisiones(mapa);

    public void testAlMenosTresVehiculos() {
        assertTrue(new Trafico(mapa).vehiculos().size() >= 3);
    }

    /** Dos minutos de simulación: nunca tocan manzanas ni salen del mapa, y giran varias veces. */
    public void testCirculanPorLasCallesYGiran() {
        Trafico trafico = new Trafico(mapa);
        float dt = 1 / 60f;
        for (int paso = 0; paso < 60 * 120; paso++) {
            trafico.actualizar(dt, null);
            for (VehiculoAutonomo vehiculo : trafico.vehiculos()) {
                assertTrue("Vehículo fuera de la calle en " + vehiculo.x() + ", " + vehiculo.z(),
                    colisiones.puedeCircular(vehiculo.x(), vehiculo.z(), VehiculoAutonomo.RADIO));
            }
        }
        for (VehiculoAutonomo vehiculo : trafico.vehiculos()) {
            assertTrue("El vehículo debe girar varias veces", vehiculo.giros() >= 4);
        }
    }

    public void testRutaRechazaTramosDiagonales() {
        try {
            new Ruta(mapa, new int[][] {{0, 0}, {2, 2}, {0, 4}});
            fail("Se esperaba una ruta inválida");
        } catch (IllegalArgumentException esperado) {
            // correcto
        }
    }

    public void testElJugadorNoAtraviesaUnVehiculo() {
        Trafico trafico = new Trafico(mapa);
        VehiculoAutonomo vehiculo = trafico.vehiculos().get(0);
        float x = vehiculo.x();
        float z = vehiculo.z();
        // Acercarse hasta tocarlo está prohibido; alejarse siempre se permite.
        assertFalse(trafico.permite(x + 5, z, x + 3, z, Auto.RADIO));
        assertTrue(trafico.permite(x + 3, z, x + 4, z, Auto.RADIO));
    }

    public void testFrenaSiElJugadorEstaDelante() {
        Trafico trafico = new Trafico(mapa);
        VehiculoAutonomo vehiculo = trafico.vehiculos().get(0);
        Auto jugador = new Auto();
        jugador.colocar(vehiculo.x() + vehiculo.direccionX() * 4, vehiculo.z() + vehiculo.direccionZ() * 4, 0, 0);
        for (int i = 0; i < 120; i++) {
            trafico.actualizar(1 / 60f, jugador);
        }
        assertEquals(0f, vehiculo.velocidad(), 0.01f);
    }
}
