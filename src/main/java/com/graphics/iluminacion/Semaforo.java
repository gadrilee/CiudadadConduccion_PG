package com.graphics.iluminacion;

import java.util.ArrayList;
import java.util.List;

import com.graphics.mundo.Manzana;
import com.graphics.mundo.MapaCiudad;

/**
 * Semáforo decorativo controlado por tiempo: rojo 5 s, verde 5 s y amarillo 2 s (ciclo de 12 s).
 * No detiene a los vehículos.
 *
 * @param desfase segundos que se adelanta el ciclo, para que no cambien todos a la vez
 */
public record Semaforo(float x, float z, float desfase) {

    /** Luz activa. El orden de la secuencia es ROJO, VERDE, AMARILLO y vuelve a ROJO. */
    public enum Fase {
        ROJO,
        VERDE,
        AMARILLO
    }

    public static final float CICLO = 12;
    private static final float FIN_ROJO = 5;
    private static final float FIN_VERDE = 10;
    private static final float ESQUINA = 4.2f;

    public Fase fase(float tiempo) {
        return faseEn(tiempo + desfase);
    }

    public static Fase faseEn(float tiempo) {
        float momento = tiempo % CICLO;
        if (momento < 0) {
            momento += CICLO;
        }
        if (momento < FIN_ROJO) {
            return Fase.ROJO;
        }
        if (momento < FIN_VERDE) {
            return Fase.VERDE;
        }
        return Fase.AMARILLO;
    }

    /**
     * Un semáforo en la esquina noreste de las manzanas que no tienen farola,
     * con desfase alternado entre filas de manzanas.
     */
    public static List<Semaforo> distribuir(MapaCiudad mapa) {
        List<Semaforo> lista = new ArrayList<>();
        for (Manzana manzana : mapa.manzanas()) {
            if ((manzana.indiceFila() + manzana.indiceColumna()) % 2 == 1) {
                float desfase = (manzana.indiceFila() % 2 == 0) ? 0 : CICLO / 2;
                lista.add(new Semaforo(manzana.x() + ESQUINA, manzana.z() - ESQUINA, desfase));
            }
        }
        return lista;
    }
}
