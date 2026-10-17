package com.graphics.mundo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Plano de la ciudad. Toda la escala (suelo, límites, cámaras, minimapa) se deriva de la matriz,
 * así que para cambiar el tamaño de la ciudad basta con editar {@link #DISENO}.
 * Filas = Z (norte arriba, -Z), columnas = X. Las filas y columnas pares son calles.
 */
public class MapaCiudad {

    /** Ancho y profundidad de cada celda, en unidades del mundo (1 unidad = 1 metro). */
    public static final float CELDA = 10;

    /** 0 = calle, 1 = edificio, 2 = parque. 11x11 celdas: 19 edificios y 6 parques. */
    public static final int[][] DISENO = {
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 1, 0, 2, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 2, 0, 1, 0, 1, 0, 1, 0, 2, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 1, 0, 2, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 2, 0, 1, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 1, 0, 1, 0, 2, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    private final TipoCelda[][] celdas;
    private final float limite;
    private final List<Manzana> manzanas;

    public MapaCiudad() {
        this(DISENO);
    }

    /** Crea el mapa a partir de una matriz cuadrada de códigos de {@link TipoCelda}. */
    public MapaCiudad(int[][] diseno) {
        int filas = diseno.length;
        celdas = new TipoCelda[filas][];
        for (int fila = 0; fila < filas; fila++) {
            if (diseno[fila].length != filas) {
                throw new IllegalArgumentException("El mapa debe ser cuadrado");
            }
            celdas[fila] = new TipoCelda[filas];
            for (int columna = 0; columna < filas; columna++) {
                celdas[fila][columna] = TipoCelda.desdeCodigo(diseno[fila][columna]);
            }
        }
        limite = filas * CELDA / 2;
        manzanas = Collections.unmodifiableList(crearManzanas());
    }

    public int filas() {
        return celdas.length;
    }

    public int columnas() {
        return celdas[0].length;
    }

    /** Distancia del origen a cada borde del mapa. */
    public float limite() {
        return limite;
    }

    /** Lado completo del mapa. */
    public float tamano() {
        return limite * 2;
    }

    /** Centro de la celda {@code indice} (fila o columna) en coordenadas del mundo. */
    public float centro(int indice) {
        return -limite + CELDA * (indice + 0.5f);
    }

    /** Índice de la celda que contiene la coordenada, o -1 si está fuera del mapa. */
    public int indice(float coordenada) {
        int indice = (int) Math.floor((coordenada + limite) / CELDA);
        return (indice >= 0 && indice < filas()) ? indice : -1;
    }

    public TipoCelda tipo(int fila, int columna) {
        return celdas[fila][columna];
    }

    public boolean esCalle(int fila, int columna) {
        return fila >= 0 && columna >= 0 && fila < filas() && columna < columnas()
            && celdas[fila][columna] == TipoCelda.CALLE;
    }

    /** ¿El punto del mundo cae sobre una celda de calle? */
    public boolean esCalleEn(float x, float z) {
        return esCalle(indice(z), indice(x));
    }

    public List<Manzana> manzanas() {
        return manzanas;
    }

    public int contar(TipoCelda tipo) {
        int total = 0;
        for (TipoCelda[] fila : celdas) {
            for (TipoCelda celda : fila) {
                if (celda == tipo) {
                    total++;
                }
            }
        }
        return total;
    }

    /** Sector al que pertenece un punto del mundo (los puntos exteriores se ajustan al borde). */
    public Sector sectorEn(float x, float z) {
        int fila = Math.max(0, Math.min(filas() - 1, (int) Math.floor((z + limite) / CELDA)));
        int columna = Math.max(0, Math.min(columnas() - 1, (int) Math.floor((x + limite) / CELDA)));
        return Sector.de(fila, columna, filas(), columnas());
    }

    private List<Manzana> crearManzanas() {
        List<Manzana> lista = new ArrayList<>();
        for (int fila = 0; fila < filas(); fila++) {
            for (int columna = 0; columna < columnas(); columna++) {
                TipoCelda tipo = celdas[fila][columna];
                if (tipo != TipoCelda.CALLE) {
                    lista.add(crearManzana(fila, columna, tipo));
                }
            }
        }
        return lista;
    }

    /** Altura y color reproducibles: el centro tiene rascacielos (14-22) y la periferia edificios bajos (5-12). */
    private Manzana crearManzana(int fila, int columna, TipoCelda tipo) {
        Sector sector = Sector.de(fila, columna, filas(), columnas());
        float altura;
        if (sector == Sector.CENTRO) {
            altura = 14 + (fila * 5 + columna * 3) % 9;
        } else {
            altura = 5 + (fila * 3 + columna * 7) % 8;
        }
        float variacion = ((fila + columna) % 3 - 1) * 0.06f;
        return new Manzana(fila, columna, tipo, centro(columna), centro(fila), altura,
            sector.rojo() + variacion, sector.verde() + variacion * 0.5f, sector.azul() - variacion, sector);
    }
}
