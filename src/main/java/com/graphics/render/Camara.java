package com.graphics.render;

import com.graphics.mundo.MapaCiudad;
import com.graphics.vehiculo.Auto;

/** Cámara de seguimiento (detrás del auto) o aérea oblicua; la aérea se escala con el tamaño del mapa. */
public class Camara {

    /** Modos que se alternan con C. */
    public enum Modo {
        SEGUIMIENTO("SEGUIMIENTO"),
        AEREA("AEREA");

        private final String nombre;

        Modo(String nombre) {
            this.nombre = nombre;
        }

        public String nombre() {
            return nombre;
        }
    }

    private static final float PLANO_LEJANO = 400;

    private Modo modo = Modo.SEGUIMIENTO;
    private float orbita = 0.6f;

    public void alternar() {
        modo = (modo == Modo.SEGUIMIENTO) ? Modo.AEREA : Modo.SEGUIMIENTO;
    }

    public Modo modo() {
        return modo;
    }

    /** Gira la vista aérea alrededor de la ciudad (se usa en el menú de inicio). */
    public void girarOrbita(float radianes) {
        orbita += radianes;
    }

    /** Envía al shader la posición, el objetivo y la proporción de la cámara. */
    public void aplicar(ProgramaShader programa, Auto auto, MapaCiudad mapa, int ancho, int alto, boolean forzarAerea) {
        if (modo == Modo.AEREA || forzarAerea) {
            float radio = mapa.limite() * 1.8f;
            float altura = mapa.limite() * 1.5f;
            programa.vector3("uOjo", (float) Math.sin(orbita) * radio, altura, (float) Math.cos(orbita) * radio);
            programa.vector3("uObjetivo", 0, 0, 0);
        } else {
            // 12 unidades detrás del auto (opuesto a su frente) y 9 de altura.
            float camaraX = auto.x() - auto.frenteX() * 12;
            float camaraZ = auto.z() - auto.frenteZ() * 12;
            programa.vector3("uOjo", camaraX, 9, camaraZ);
            programa.vector3("uObjetivo", auto.x(), 0.8f, auto.z());
        }
        programa.decimal("uAspecto", (float) ancho / alto);
        programa.decimal("uLejos", PLANO_LEJANO);
    }
}
