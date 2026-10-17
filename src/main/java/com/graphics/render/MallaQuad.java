package com.graphics.render;

import static org.lwjgl.opengl.GL33.*;

/** Cuadrado unitario 2D (de 0 a 1) usado por la interfaz para dibujar rectángulos en píxeles. */
public class MallaQuad {

    private final int vao;
    private final int vbo;

    public MallaQuad() {
        float[] vertices = {
            0, 0,  1, 0,  1, 1,
            1, 1,  0, 1,  0, 0
        };
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0L);
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void enlazar() {
        glBindVertexArray(vao);
    }

    public void dibujar() {
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }

    public void eliminar() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }
}
