package com.graphics.render;

import java.util.List;

import com.graphics.iluminacion.Farola;
import com.graphics.iluminacion.Iluminacion;
import com.graphics.vehiculo.Auto;

/** Envía la iluminación al shader y dibuja los postes y bombillas de las farolas. */
public class DibujoLuces {

    private final Renderizador render;

    public DibujoLuces(Renderizador render) {
        this.render = render;
    }

    /** Uniforms que usa escena.frag: modo, farolas y posición/dirección de los faros. */
    public void enviar(Iluminacion iluminacion, Auto auto) {
        ProgramaShader programa = render.programa();
        programa.entero("uNoche", iluminacion.esNoche() ? 1 : 0);
        programa.entero("uFaros", iluminacion.farosEncendidos() ? 1 : 0);
        programa.entero("uEmision", 0);
        List<Farola> farolas = iluminacion.farolas();
        programa.entero("uNumFarolas", farolas.size());
        for (int i = 0; i < farolas.size(); i++) {
            Farola farola = farolas.get(i);
            programa.vector3("uFarolas[" + i + "]", farola.x(), farola.y(), farola.z());
        }
        programa.vector3("uAuto", auto.x(), 0.7f, auto.z());
        programa.vector3("uFrente", auto.frenteX(), 0, auto.frenteZ());
    }

    /** La bombilla solo "brilla" (emisión) de noche; la luz sobre el entorno la calcula el shader. */
    public void dibujarFarolas(Iluminacion iluminacion) {
        for (Farola farola : iluminacion.farolas()) {
            render.caja(farola.x(), 2.2f, farola.z(), 0.18f, 4.4f, 0.18f, 0.20f, 0.24f, 0.28f);
            render.emision(iluminacion.esNoche());
            render.caja(farola.x(), farola.y(), farola.z(), 0.7f, 0.35f, 0.7f, 1, 0.83f, 0.42f);
            render.emision(false);
        }
    }
}
