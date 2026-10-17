package com.graphics.mundo;

/**
 * Colisiones de un círculo (el vehículo) contra los bordes del mapa y las manzanas (incluida la acera).
 */
public class Colisiones implements Transitable {

    private final MapaCiudad mapa;

    public Colisiones(MapaCiudad mapa) {
        this.mapa = mapa;
    }

    /** ¿Cabe un círculo de {@code radio} en (x, z) sin tocar manzanas ni salir del mapa? */
    public boolean puedeCircular(float x, float z, float radio) {
        float limitePermitido = mapa.limite() - radio;
        if (Math.abs(x) > limitePermitido || Math.abs(z) > limitePermitido) {
            return false;
        }
        float mitadManzana = MapaCiudad.CELDA / 2;
        for (Manzana manzana : mapa.manzanas()) {
            // Punto del rectángulo de la manzana más cercano al centro del círculo.
            float cercaX = Math.max(manzana.x() - mitadManzana, Math.min(x, manzana.x() + mitadManzana));
            float cercaZ = Math.max(manzana.z() - mitadManzana, Math.min(z, manzana.z() + mitadManzana));
            float distanciaX = x - cercaX;
            float distanciaZ = z - cercaZ;
            // Comparar distancias al cuadrado evita calcular la raíz.
            if (distanciaX * distanciaX + distanciaZ * distanciaZ < radio * radio) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean permite(float desdeX, float desdeZ, float haciaX, float haciaZ, float radio) {
        return puedeCircular(haciaX, haciaZ, radio);
    }
}
