package com.graphics.trafico;

import java.util.Collections;
import java.util.List;

import com.graphics.mundo.MapaCiudad;
import com.graphics.mundo.Transitable;
import com.graphics.vehiculo.Auto;

/**
 * Conjunto de autos autónomos. Cada uno frena si tiene algo justo delante y el jugador
 * no puede atravesarlos (implementa {@link Transitable}).
 */
public class Trafico implements Transitable {

    /** Distancia hacia delante en la que un auto detecta un obstáculo. */
    private static final float ALCANCE_DETECCION = 7;
    /** Ancho del "pasillo" delante del auto: menor que la separación entre carriles opuestos (5). */
    private static final float ANCHO_DETECCION = 3.4f;

    private final List<VehiculoAutonomo> vehiculos;

    public Trafico(MapaCiudad mapa) {
        // Rutas en sentido horario por cruces {fila, columna}.
        Ruta anilloExterior = new Ruta(mapa, new int[][] {{0, 0}, {0, 10}, {10, 10}, {10, 0}});
        Ruta anilloCentral = new Ruta(mapa, new int[][] {{2, 2}, {2, 8}, {8, 8}, {8, 2}});
        Ruta recorridoEste = new Ruta(mapa, new int[][] {{4, 4}, {4, 10}, {10, 10}, {10, 6}, {6, 6}, {6, 4}});
        Ruta recorridoOeste = new Ruta(mapa, new int[][] {{0, 0}, {0, 4}, {8, 4}, {8, 0}});
        vehiculos = List.of(
            new VehiculoAutonomo(anilloExterior, 1, 7.0f, 0.15f, 0.35f, 0.85f),
            new VehiculoAutonomo(anilloCentral, 0, 6.0f, 0.95f, 0.78f, 0.10f),
            new VehiculoAutonomo(recorridoEste, 0, 6.5f, 0.20f, 0.70f, 0.30f),
            new VehiculoAutonomo(recorridoOeste, 2, 5.5f, 0.85f, 0.85f, 0.88f)
        );
    }

    /** @param jugador auto del usuario, o {@code null} para simular solo el tráfico */
    public void actualizar(float deltaTime, Auto jugador) {
        for (VehiculoAutonomo vehiculo : vehiculos) {
            boolean jugadorDelante = jugador != null && estaDelante(vehiculo, jugador.x(), jugador.z());
            boolean vehiculoDelante = false;
            for (VehiculoAutonomo otro : vehiculos) {
                if (otro != vehiculo && estaDelante(vehiculo, otro.x(), otro.z())) {
                    vehiculoDelante = true;
                    break;
                }
            }
            vehiculo.actualizar(deltaTime, jugadorDelante, vehiculoDelante);
        }
    }

    /** Proyecta el obstáculo sobre la dirección de avance: distancia hacia delante y desvío lateral. */
    private static boolean estaDelante(VehiculoAutonomo vehiculo, float obstaculoX, float obstaculoZ) {
        float dx = obstaculoX - vehiculo.x();
        float dz = obstaculoZ - vehiculo.z();
        float frenteX = vehiculo.direccionX();
        float frenteZ = vehiculo.direccionZ();
        float adelante = dx * frenteX + dz * frenteZ;
        float lateral = Math.abs(dx * frenteZ - dz * frenteX);
        return adelante > 0 && adelante < ALCANCE_DETECCION && lateral < ANCHO_DETECCION;
    }

    /** Bloquea al jugador si se acercaría a un auto más de la suma de radios (alejarse siempre se permite). */
    @Override
    public boolean permite(float desdeX, float desdeZ, float haciaX, float haciaZ, float radio) {
        float minimo = radio + VehiculoAutonomo.RADIO;
        for (VehiculoAutonomo vehiculo : vehiculos) {
            float nueva = distanciaCuadrada(haciaX, haciaZ, vehiculo);
            float actual = distanciaCuadrada(desdeX, desdeZ, vehiculo);
            if (nueva < minimo * minimo && nueva < actual) {
                return false;
            }
        }
        return true;
    }

    private static float distanciaCuadrada(float x, float z, VehiculoAutonomo vehiculo) {
        float dx = x - vehiculo.x();
        float dz = z - vehiculo.z();
        return dx * dx + dz * dz;
    }

    public void reiniciar() {
        for (VehiculoAutonomo vehiculo : vehiculos) {
            vehiculo.reiniciar();
        }
    }

    public List<VehiculoAutonomo> vehiculos() {
        return Collections.unmodifiableList(vehiculos);
    }
}
