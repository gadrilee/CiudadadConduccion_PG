package com.graphics;

import com.graphics.app.Aplicacion;

/** Punto de entrada: {@code mvn compile exec:exec}. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        new Aplicacion().run();
    }
}
