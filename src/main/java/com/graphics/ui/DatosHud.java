package com.graphics.ui;

/** Información que muestra el panel del HUD en un cuadro (se arma en la aplicación a partir del modelo). */
public record DatosHud(int kilometrosPorHora, boolean faros, boolean noche, String camara,
                       int entregas, int totalEntregas, int segundos, String sector, boolean claxon,
                       boolean minimapa) {
}
