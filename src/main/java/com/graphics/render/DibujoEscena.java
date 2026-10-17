package com.graphics.render;

import java.util.List;

import com.graphics.iluminacion.Iluminacion;
import com.graphics.iluminacion.Semaforo;
import com.graphics.mundo.MapaCiudad;
import com.graphics.trafico.Trafico;
import com.graphics.trafico.VehiculoAutonomo;
import com.graphics.vehiculo.Auto;

/**
 * Compone la escena 3D completa. En el minimapa solo dibuja la ciudad base;
 * los detalles pequeños, las farolas y los autos se reservan para la vista principal.
 */
public class DibujoEscena {

    private final Renderizador render;
    private final DibujoCiudad ciudad;
    private final Decoracion decoracion;
    private final DibujoLuces luces;
    private final DibujoVehiculo vehiculo;

    public DibujoEscena(Renderizador render, MapaCiudad mapa) {
        this.render = render;
        ciudad = new DibujoCiudad(render, mapa);
        decoracion = new Decoracion(render, mapa);
        luces = new DibujoLuces(render);
        vehiculo = new DibujoVehiculo(render);
    }

    public DibujoLuces luces() {
        return luces;
    }

    /**
     * @param destino destino activo de las entregas, o {@code null} si ya no hay
     * @param tiempo  segundos de animación (semáforos y baliza)
     */
    public void dibujar(Auto auto, Iluminacion iluminacion, Trafico trafico, float[] destino,
                        List<Semaforo> semaforos, float tiempo) {
        ciudad.dibujar();
        if (render.modoMapa()) {
            return;
        }
        decoracion.dibujar(iluminacion.esNoche(), tiempo, semaforos);
        luces.dibujarFarolas(iluminacion);
        vehiculo.dibujar(auto.x(), auto.z(), auto.angulo(), 0.95f, 0.24f, 0.12f, iluminacion.farosEncendidos());
        for (VehiculoAutonomo otro : trafico.vehiculos()) {
            vehiculo.dibujar(otro.x(), otro.z(), otro.angulo(), otro.rojo(), otro.verde(), otro.azul(),
                iluminacion.esNoche());
        }
        if (destino != null) {
            dibujarDestino(destino[0], destino[1], tiempo);
        }
    }

    /** Plataforma dorada sobre el asfalto y una baliza que flota y gira; ambas emisivas para verse de noche. */
    private void dibujarDestino(float x, float z, float tiempo) {
        render.emision(true);
        render.caja(x, 0.06f, z, 5, 0.08f, 5, 1, 0.72f, 0.12f);
        float altura = 3.5f + (float) Math.sin(tiempo * 2) * 0.3f;
        render.cajaGirada(x, altura, z, 0.8f, 0.8f, 0.8f, 1, 0.8f, 0.15f, tiempo);
        render.emision(false);
    }
}
