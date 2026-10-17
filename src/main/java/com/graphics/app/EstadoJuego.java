package com.graphics.app;

/** Fases de la partida; determinan qué se actualiza y qué menú se superpone. */
public enum EstadoJuego {
    /** Pantalla de inicio: la ciudad se anima, pero el auto no se controla. */
    INICIO,
    /** Partida en curso. */
    JUGANDO,
    /** Todo se detiene hasta volver a presionar P. */
    PAUSA,
    /** Todas las entregas completadas; se puede seguir conduciendo o reiniciar con R. */
    VICTORIA
}
