package com.graphics.trafico;

/**
 * Auto que recorre una {@link Ruta} sin intervención del usuario. La posición avanza exactamente
 * sobre los tramos (nunca sale de la calle) y la orientación gira suave hacia el tramo actual.
 */
public class VehiculoAutonomo {

    public static final float RADIO = 1.65f;

    private static final float ACELERACION = 6;
    private static final float FRENADO = 14;
    private static final float GIRO_MAXIMO = 3.0f;
    private static final float DISTANCIA_CURVA = 5;
    /** Segundos bloqueado por otro auto antes de ignorarlo un momento (evita bloqueos mutuos). */
    private static final float PACIENCIA = 3;
    private static final float TIEMPO_IGNORAR = 2;

    private final Ruta ruta;
    private final int puntoInicial;
    private final float velocidadCrucero;
    private final float rojo;
    private final float verde;
    private final float azul;

    private float x;
    private float z;
    private float angulo;
    private float velocidad;
    private int destino;
    private int giros;
    private float espera;
    private float ignorarVehiculos;

    public VehiculoAutonomo(Ruta ruta, int puntoInicial, float velocidadCrucero, float rojo, float verde, float azul) {
        this.ruta = ruta;
        this.puntoInicial = puntoInicial;
        this.velocidadCrucero = velocidadCrucero;
        this.rojo = rojo;
        this.verde = verde;
        this.azul = azul;
        reiniciar();
    }

    /** Vuelve al punto inicial de su ruta, detenido y mirando hacia el siguiente punto. */
    public void reiniciar() {
        x = ruta.x(puntoInicial);
        z = ruta.z(puntoInicial);
        destino = puntoInicial + 1;
        velocidad = 0;
        giros = 0;
        espera = 0;
        ignorarVehiculos = 0;
        angulo = rumbo(ruta.x(destino) - x, ruta.z(destino) - z);
    }

    /**
     * @param jugadorDelante  el jugador está justo delante: siempre se detiene
     * @param vehiculoDelante otro auto autónomo está delante: se detiene, con paciencia limitada
     */
    public void actualizar(float deltaTime, boolean jugadorDelante, boolean vehiculoDelante) {
        if (ignorarVehiculos > 0) {
            ignorarVehiculos -= deltaTime;
            vehiculoDelante = false;
        }
        if (vehiculoDelante) {
            espera += deltaTime;
            if (espera > PACIENCIA) {
                ignorarVehiculos = TIEMPO_IGNORAR;
                espera = 0;
            }
        } else {
            espera = 0;
        }

        float destinoX = ruta.x(destino);
        float destinoZ = ruta.z(destino);
        float dx = destinoX - x;
        float dz = destinoZ - z;
        float distancia = (float) Math.sqrt(dx * dx + dz * dz);

        float objetivo = velocidadCrucero;
        if (distancia < DISTANCIA_CURVA) {
            objetivo *= 0.55f;
        }
        if (jugadorDelante || vehiculoDelante) {
            objetivo = 0;
        }
        float cambioMaximo = (objetivo < velocidad ? FRENADO : ACELERACION) * deltaTime;
        velocidad += Math.max(-cambioMaximo, Math.min(cambioMaximo, objetivo - velocidad));

        float paso = velocidad * deltaTime;
        if (paso >= distancia) {
            x = destinoX;
            z = destinoZ;
            destino++;
            giros++;
        } else if (distancia > 0) {
            x += dx / distancia * paso;
            z += dz / distancia * paso;
        }

        // Gira hacia el tramo actual a una velocidad angular máxima.
        float rumboX = ruta.x(destino) - x;
        float rumboZ = ruta.z(destino) - z;
        if (rumboX * rumboX + rumboZ * rumboZ > 1e-6f) {
            float diferencia = normalizar(rumbo(rumboX, rumboZ) - angulo);
            float giroMaximo = GIRO_MAXIMO * deltaTime;
            angulo += Math.max(-giroMaximo, Math.min(giroMaximo, diferencia));
        }
    }

    /** Ángulo cuyo frente (-sen, -cos) apunta en la dirección (dx, dz). */
    private static float rumbo(float dx, float dz) {
        return (float) Math.atan2(-dx, -dz);
    }

    /** Lleva un ángulo al intervalo [-PI, PI] para girar siempre por el lado más corto. */
    private static float normalizar(float angulo) {
        return (float) Math.IEEEremainder(angulo, Math.PI * 2);
    }

    /** Dirección unitaria de avance (hacia el próximo punto de la ruta). */
    public float direccionX() {
        float dx = ruta.x(destino) - x;
        float dz = ruta.z(destino) - z;
        float largo = (float) Math.sqrt(dx * dx + dz * dz);
        return largo > 1e-4f ? dx / largo : -(float) Math.sin(angulo);
    }

    public float direccionZ() {
        float dx = ruta.x(destino) - x;
        float dz = ruta.z(destino) - z;
        float largo = (float) Math.sqrt(dx * dx + dz * dz);
        return largo > 1e-4f ? dz / largo : -(float) Math.cos(angulo);
    }

    public float x() {
        return x;
    }

    public float z() {
        return z;
    }

    public float angulo() {
        return angulo;
    }

    public float velocidad() {
        return velocidad;
    }

    /** Cantidad de puntos de ruta alcanzados (cada uno implica un giro o cambio de tramo). */
    public int giros() {
        return giros;
    }

    public float rojo() {
        return rojo;
    }

    public float verde() {
        return verde;
    }

    public float azul() {
        return azul;
    }
}
