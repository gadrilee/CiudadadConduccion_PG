package com.graphics.render;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL33.*;

/**
 * Programa GLSL (vértices + fragmentos) cargado desde {@code src/main/resources}.
 * Guarda en caché la ubicación de cada uniform para no consultarla en cada dibujo.
 */
public class ProgramaShader {

    private final int id;
    private final Map<String, Integer> uniforms = new HashMap<>();

    /** Compila y enlaza los dos shaders indicados por su ruta en el classpath. */
    public ProgramaShader(String rutaVertices, String rutaFragmentos) {
        int vertices = compilar(GL_VERTEX_SHADER, leerRecurso(rutaVertices));
        int fragmentos = 0;
        int programa = 0;
        try {
            fragmentos = compilar(GL_FRAGMENT_SHADER, leerRecurso(rutaFragmentos));
            programa = glCreateProgram();
            glAttachShader(programa, vertices);
            glAttachShader(programa, fragmentos);
            glLinkProgram(programa);
            if (glGetProgrami(programa, GL_LINK_STATUS) == GL_FALSE) {
                String error = glGetProgramInfoLog(programa);
                glDeleteProgram(programa);
                throw new IllegalStateException("Error al enlazar " + rutaVertices + ": " + error);
            }
        } finally {
            glDeleteShader(vertices);
            if (fragmentos != 0) {
                glDeleteShader(fragmentos);
            }
        }
        id = programa;
    }

    /** Activa el programa para los siguientes dibujos. */
    public void usar() {
        glUseProgram(id);
    }

    public void entero(String nombre, int valor) {
        glUniform1i(ubicacion(nombre), valor);
    }

    public void decimal(String nombre, float valor) {
        glUniform1f(ubicacion(nombre), valor);
    }

    public void vector2(String nombre, float x, float y) {
        glUniform2f(ubicacion(nombre), x, y);
    }

    public void vector3(String nombre, float x, float y, float z) {
        glUniform3f(ubicacion(nombre), x, y, z);
    }

    public void vector4(String nombre, float x, float y, float z, float w) {
        glUniform4f(ubicacion(nombre), x, y, z, w);
    }

    /** Libera el programa de la GPU. */
    public void eliminar() {
        glDeleteProgram(id);
    }

    /** Devuelve la ubicación del uniform; -1 significa que el shader no lo usa y OpenGL ignora el envío. */
    private int ubicacion(String nombre) {
        return uniforms.computeIfAbsent(nombre, n -> glGetUniformLocation(id, n));
    }

    private static int compilar(int tipo, String fuente) {
        int shader = glCreateShader(tipo);
        glShaderSource(shader, fuente);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            String error = glGetShaderInfoLog(shader);
            glDeleteShader(shader);
            throw new IllegalStateException("Error GLSL: " + error);
        }
        return shader;
    }

    private static String leerRecurso(String ruta) {
        try (InputStream entrada = ProgramaShader.class.getResourceAsStream(ruta)) {
            if (entrada == null) {
                throw new IllegalStateException("No se encontró el recurso " + ruta);
            }
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
