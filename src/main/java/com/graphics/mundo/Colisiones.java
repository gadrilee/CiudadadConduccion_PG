package com.graphics.mundo;

import java.util.List;

/**
 * Colisiones de un vehículo (rectángulo girado) contra los bordes del mapa y las manzanas (incluida la acera).
 */
public class Colisiones implements Transitable {

    private final MapaCiudad mapa;
    private final List<Rectangulo> manzanas;

    public Colisiones(MapaCiudad mapa) {
        this.mapa = mapa;
        float mitad = MapaCiudad.CELDA / 2;
        manzanas = mapa.manzanas().stream()
            .map(manzana -> new Rectangulo(manzana.x(), manzana.z(), 0, mitad, mitad))
            .toList();
    }

    /** ¿Cabe el rectángulo sin tocar manzanas ni salir del mapa? */
    public boolean puedeUbicar(Rectangulo forma) {
        float limite = mapa.limite();
        // Lo que sobresale el rectángulo en X y en Z según su giro.
        if (Math.abs(forma.x()) + forma.alcance(1, 0) > limite || Math.abs(forma.z()) + forma.alcance(0, 1) > limite) {
            return false;
        }
        for (Rectangulo manzana : manzanas) {
            if (forma.choca(manzana)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean permite(Rectangulo desde, Rectangulo hacia) {
        return puedeUbicar(hacia);
    }
}
