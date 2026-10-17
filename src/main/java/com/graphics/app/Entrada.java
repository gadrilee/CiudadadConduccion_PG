package com.graphics.app;

import com.graphics.vehiculo.ControlesAuto;

import static org.lwjgl.glfw.GLFW.*;

/** Traduce el estado continuo del teclado (teclas mantenidas) a controles del auto. */
public class Entrada {

    private final long ventana;

    public Entrada(long ventana) {
        this.ventana = ventana;
    }

    public boolean pulsada(int tecla) {
        return glfwGetKey(ventana, tecla) == GLFW_PRESS;
    }

    /** W/S o flechas para acelerar, A/D o flechas para girar y espacio para frenar. */
    public ControlesAuto controlesAuto() {
        float acelerador = 0;
        if (pulsada(GLFW_KEY_W) || pulsada(GLFW_KEY_UP)) {
            acelerador += 1;
        }
        if (pulsada(GLFW_KEY_S) || pulsada(GLFW_KEY_DOWN)) {
            acelerador -= 1;
        }
        float direccion = 0;
        if (pulsada(GLFW_KEY_A) || pulsada(GLFW_KEY_LEFT)) {
            direccion += 1;
        }
        if (pulsada(GLFW_KEY_D) || pulsada(GLFW_KEY_RIGHT)) {
            direccion -= 1;
        }
        return new ControlesAuto(acelerador, direccion, pulsada(GLFW_KEY_SPACE));
    }
}
