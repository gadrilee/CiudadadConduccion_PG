package com.graphics.mundo;

/**
 * Rectángulo en el plano XZ, centrado en (x, z) y girado {@code angulo} (misma convención que los
 * vehículos: el largo va hacia el frente (-sen a, -cos a)). Es la forma de colisión de autos, chata y manzanas.
 */
public record Rectangulo(float x, float z, float angulo, float mitadAncho, float mitadLargo) {

    /**
     * Teorema del eje separador: dos rectángulos no se tocan si existe un eje en el que sus "sombras"
     * no se solapan. Basta probar los 4 ejes de sus lados (2 de cada uno).
     */
    public boolean choca(Rectangulo otro) {
        return !separadosEnSusEjes(this, otro) && !separadosEnSusEjes(otro, this);
    }

    /** Prueba los ejes lateral y frontal de {@code dueno}. */
    private static boolean separadosEnSusEjes(Rectangulo dueno, Rectangulo otro) {
        float seno = (float) Math.sin(dueno.angulo);
        float coseno = (float) Math.cos(dueno.angulo);
        return separadosEn(dueno, otro, coseno, -seno) || separadosEn(dueno, otro, -seno, -coseno);
    }

    private static boolean separadosEn(Rectangulo a, Rectangulo b, float ejeX, float ejeZ) {
        // Distancia entre centros medida sobre el eje, contra la suma de las medias sombras.
        float distancia = Math.abs((b.x - a.x) * ejeX + (b.z - a.z) * ejeZ);
        return distancia >= a.alcance(ejeX, ejeZ) + b.alcance(ejeX, ejeZ);
    }

    /** Media sombra del rectángulo sobre un eje unitario: cuánto se extiende desde su centro en esa dirección. */
    public float alcance(float ejeX, float ejeZ) {
        float seno = (float) Math.sin(angulo);
        float coseno = (float) Math.cos(angulo);
        // Ejes propios: lateral (cos a, -sen a) y frente (-sen a, -cos a).
        float sobreLateral = Math.abs(coseno * ejeX - seno * ejeZ);
        float sobreFrente = Math.abs(-seno * ejeX - coseno * ejeZ);
        return mitadAncho * sobreLateral + mitadLargo * sobreFrente;
    }

    /** El mismo rectángulo con {@code margen} más por cada lado. */
    public Rectangulo agrandado(float margen) {
        return new Rectangulo(x, z, angulo, mitadAncho + margen, mitadLargo + margen);
    }

    /** Distancia al cuadrado entre los centros. */
    public float distanciaCuadrada(Rectangulo otro) {
        float dx = otro.x - x;
        float dz = otro.z - z;
        return dx * dx + dz * dz;
    }
}
