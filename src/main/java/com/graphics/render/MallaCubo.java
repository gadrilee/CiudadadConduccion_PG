package com.graphics.render;

import static org.lwjgl.opengl.GL33.*;

/**
 * Cubo unitario centrado en el origen con 36 vértices explícitos (posición + normal).
 * Todos los objetos 3D de la ciudad son este cubo escalado, girado y trasladado.
 */
public class MallaCubo {

    private static final int VERTICES = 36;

    private final int vao;
    private final int vbo;

    /** Copia los vértices a la GPU y describe sus atributos en un VAO. */
    public MallaCubo() {
        float[] vertices = {
            // Cara frontal (+Z)
            -0.5f, -0.5f, 0.5f, 0, 0, 1,   0.5f, -0.5f, 0.5f, 0, 0, 1,   0.5f, 0.5f, 0.5f, 0, 0, 1,
            0.5f, 0.5f, 0.5f, 0, 0, 1,     -0.5f, 0.5f, 0.5f, 0, 0, 1,   -0.5f, -0.5f, 0.5f, 0, 0, 1,
            // Cara trasera (-Z)
            -0.5f, -0.5f, -0.5f, 0, 0, -1, -0.5f, 0.5f, -0.5f, 0, 0, -1, 0.5f, 0.5f, -0.5f, 0, 0, -1,
            0.5f, 0.5f, -0.5f, 0, 0, -1,   0.5f, -0.5f, -0.5f, 0, 0, -1, -0.5f, -0.5f, -0.5f, 0, 0, -1,
            // Cara izquierda (-X)
            -0.5f, 0.5f, 0.5f, -1, 0, 0,   -0.5f, 0.5f, -0.5f, -1, 0, 0, -0.5f, -0.5f, -0.5f, -1, 0, 0,
            -0.5f, -0.5f, -0.5f, -1, 0, 0, -0.5f, -0.5f, 0.5f, -1, 0, 0, -0.5f, 0.5f, 0.5f, -1, 0, 0,
            // Cara derecha (+X)
            0.5f, 0.5f, 0.5f, 1, 0, 0,     0.5f, -0.5f, 0.5f, 1, 0, 0,   0.5f, -0.5f, -0.5f, 1, 0, 0,
            0.5f, -0.5f, -0.5f, 1, 0, 0,   0.5f, 0.5f, -0.5f, 1, 0, 0,   0.5f, 0.5f, 0.5f, 1, 0, 0,
            // Cara superior (+Y)
            -0.5f, 0.5f, -0.5f, 0, 1, 0,   -0.5f, 0.5f, 0.5f, 0, 1, 0,   0.5f, 0.5f, 0.5f, 0, 1, 0,
            0.5f, 0.5f, 0.5f, 0, 1, 0,     0.5f, 0.5f, -0.5f, 0, 1, 0,   -0.5f, 0.5f, -0.5f, 0, 1, 0,
            // Cara inferior (-Y)
            -0.5f, -0.5f, -0.5f, 0, -1, 0, 0.5f, -0.5f, -0.5f, 0, -1, 0, 0.5f, -0.5f, 0.5f, 0, -1, 0,
            0.5f, -0.5f, 0.5f, 0, -1, 0,   -0.5f, -0.5f, 0.5f, 0, -1, 0, -0.5f, -0.5f, -0.5f, 0, -1, 0
        };
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        int bytesPorVertice = 6 * Float.BYTES;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, bytesPorVertice, 0L);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, bytesPorVertice, 3L * Float.BYTES);
        glEnableVertexAttribArray(1);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void enlazar() {
        glBindVertexArray(vao);
    }

    public void dibujar() {
        glDrawArrays(GL_TRIANGLES, 0, VERTICES);
    }

    public void eliminar() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }
}
