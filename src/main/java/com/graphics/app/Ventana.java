package com.graphics.app;

import java.util.function.IntConsumer;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

/** Ventana GLFW con contexto OpenGL 3.3 Core; conoce el tamaño real del framebuffer. */
public class Ventana {

    private final GLFWErrorCallback errores;
    private final long id;
    private final int[] anchoReal = new int[1];
    private final int[] altoReal = new int[1];
    private int ancho;
    private int alto;

    public Ventana(int ancho, int alto, String titulo) {
        errores = GLFWErrorCallback.createPrint(System.err);
        errores.set();
        if (!glfwInit()) {
            throw new IllegalStateException("No se pudo iniciar GLFW");
        }
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        id = glfwCreateWindow(ancho, alto, titulo, 0, 0);
        if (id == 0) {
            glfwTerminate();
            throw new IllegalStateException("No se pudo crear la ventana OpenGL");
        }
        glfwMakeContextCurrent(id);
        glfwSwapInterval(1);
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        actualizarTamano();
    }

    /** Registra la acción que recibe cada tecla presionada (sin repeticiones). */
    public void alPresionar(IntConsumer accion) {
        glfwSetKeyCallback(id, (ventana, tecla, scancode, accionTecla, mods) -> {
            if (accionTecla == GLFW_PRESS) {
                accion.accept(tecla);
            }
        });
    }

    /** Lee el tamaño en píxeles del framebuffer (distinto del de la ventana en pantallas HiDPI). */
    public void actualizarTamano() {
        glfwGetFramebufferSize(id, anchoReal, altoReal);
        ancho = anchoReal[0];
        alto = altoReal[0];
    }

    public int ancho() {
        return ancho;
    }

    public int alto() {
        return alto;
    }

    public boolean minimizada() {
        return ancho <= 0 || alto <= 0;
    }

    public long id() {
        return id;
    }

    public double tiempo() {
        return glfwGetTime();
    }

    public void procesarEventos() {
        glfwPollEvents();
    }

    public void presentar() {
        glfwSwapBuffers(id);
    }

    public boolean debeCerrar() {
        return glfwWindowShouldClose(id);
    }

    public void cerrar() {
        glfwSetWindowShouldClose(id, true);
    }

    public void titulo(String texto) {
        glfwSetWindowTitle(id, texto);
    }

    /** Libera callbacks, ventana y GLFW. */
    public void destruir() {
        Callbacks.glfwFreeCallbacks(id);
        glfwDestroyWindow(id);
        glfwTerminate();
        glfwSetErrorCallback(null);
        errores.free();
    }
}
