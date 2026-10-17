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
                    colisiones.puedeUbicar(vehiculo.forma()));
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
        float a = vehiculo.angulo();
        float ladoX = (float) Math.cos(a);
        float ladoZ = -(float) Math.sin(a);
        // Acercarse por el costado hasta solaparse está prohibido; si ya se tocan, alejarse se permite.
        assertFalse(trafico.permite(Auto.forma(x + ladoX * 5, z + ladoZ * 5, a), Auto.forma(x + ladoX, z + ladoZ, a)));
        assertTrue(trafico.permite(Auto.forma(x + ladoX, z + ladoZ, a), Auto.forma(x + ladoX * 2, z + ladoZ * 2, a)));
    }

    /** Lado a lado, en paralelo: cabe con solo unos centímetros de separación (antes el círculo lo impedía). */
    public void testSePuedePasarAlLadoDeUnVehiculo() {
        Trafico trafico = new Trafico(mapa);
        VehiculoAutonomo vehiculo = trafico.vehiculos().get(0);
        float a = vehiculo.angulo();
        float separacion = 2 * Auto.MEDIO_ANCHO + 0.05f;
        float x = vehiculo.x() + (float) Math.cos(a) * separacion;
        float z = vehiculo.z() - (float) Math.sin(a) * separacion;
        assertTrue(trafico.permite(Auto.forma(x, z, a), Auto.forma(x + vehiculo.direccionX() * 0.1f,
            z + vehiculo.direccionZ() * 0.1f, a)));
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
