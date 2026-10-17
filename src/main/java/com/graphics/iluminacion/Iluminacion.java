package com.graphics.iluminacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.graphics.mundo.Manzana;
import com.graphics.mundo.MapaCiudad;

/** Estado de las luces: ambiente día/noche (N), faros (F) y la ubicación de las farolas. */
public class Iluminacion {

    /** Debe coincidir con MAX_FAROLAS en escena.frag. */
    public static final int MAX_FAROLAS = 16;
    public static final float ALTURA_BOMBILLA = 4.5f;
    /** Distancia de la farola al centro de la manzana: queda sobre la acera, junto a la calle. */
    private static final float ESQUINA = 4.2f;

    private final List<Farola> farolas;
    private boolean noche = true;
    private boolean faros = true;

    public Iluminacion(MapaCiudad mapa) {
        farolas = Collections.unmodifiableList(distribuir(mapa));
    }

    /**
     * Una farola en la esquina noroeste de las manzanas alternas (como un tablero de ajedrez),
     * así quedan repartidas por toda la ciudad, incluidos los sectores nuevos.
     */
    private static List<Farola> distribuir(MapaCiudad mapa) {
        List<Farola> lista = new ArrayList<>();
        for (Manzana manzana : mapa.manzanas()) {
            boolean alterna = (manzana.indiceFila() + manzana.indiceColumna()) % 2 == 0;
            if (alterna && lista.size() < MAX_FAROLAS) {
                lista.add(new Farola(manzana.x() - ESQUINA, ALTURA_BOMBILLA, manzana.z() - ESQUINA));
            }
        }
        return lista;
    }

    public void alternarNoche() {
        noche = !noche;
    }

    public void alternarFaros() {
        faros = !faros;
    }

    public boolean esNoche() {
        return noche;
    }

    public boolean farosEncendidos() {
        return faros;
    }

    public List<Farola> farolas() {
        return farolas;
    }
}
