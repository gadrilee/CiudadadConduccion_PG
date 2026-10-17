package com.graphics.app;

import java.util.List;

import com.graphics.iluminacion.Iluminacion;
import com.graphics.iluminacion.Semaforo;
import com.graphics.juego.Entregas;
import com.graphics.mundo.Colisiones;
import com.graphics.mundo.MapaCiudad;
import com.graphics.mundo.Transitable;
import com.graphics.render.Camara;
import com.graphics.render.DibujoEscena;
import com.graphics.render.Minimapa;
import com.graphics.render.Renderizador;
import com.graphics.trafico.Trafico;
import com.graphics.ui.DatosHud;
import com.graphics.ui.Hud;
import com.graphics.ui.Menu;
import com.graphics.vehiculo.Auto;
import com.graphics.vehiculo.Chata;
import com.graphics.vehiculo.ControlesAuto;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

/**
 * Orquesta la aplicación: crea el modelo y los dibujantes, atiende el teclado y ejecuta el ciclo
 * eventos -> actualizar -> escena 3D -> minimapa -> HUD -> presentar.
 */
public class Aplicacion {

    /** Paso máximo de simulación: evita atravesar manzanas tras una pausa larga del sistema. */
    private static final float DELTA_MAXIMO = 0.05f;
    private static final float DURACION_AVISO = 2.5f;
    private static final float INTERVALO_TITULO = 0.25f;
    private static final float PAUSA_ENTRE_CHOQUES = 0.7f;

    // Modelo (sin OpenGL).
    private final MapaCiudad mapa = new MapaCiudad();
    private final Colisiones colisiones = new Colisiones(mapa);
    private final Chata chata = new Chata();
    private final Auto auto = new Auto(chata);
    private final Iluminacion iluminacion = new Iluminacion(mapa);
    private final List<Semaforo> semaforos = Semaforo.distribuir(mapa);
    private final Entregas entregas = new Entregas();
    private final Trafico trafico = new Trafico(mapa);
    private final Transitable via = colisiones.y(trafico);

    // Vista y estado de la interfaz.
    private final Camara camara = new Camara();
    private final Menu menu = new Menu();
    private Ventana ventana;
    private Entrada entrada;
    private Renderizador render;
    private DibujoEscena escena;
    private Minimapa minimapa;
    private Hud hud;
    private Sonidos sonidos;

    private EstadoJuego estado = EstadoJuego.INICIO;
    private boolean mostrarMapa = true;
    private boolean mostrarAyuda = false;
    private float tiempoMundo;
    private float tiempoAviso;
    private String aviso = "";
    private float tiempoTitulo;
    private float esperaChoque;
    private boolean frenoSonando;

    /** Inicia, ejecuta el ciclo y libera los recursos incluso si ocurre un error. */
    public void run() {
        try {
            iniciar();
            bucle();
        } finally {
            limpiar();
        }
    }

    private void iniciar() {
        ventana = new Ventana(1100, 760, "Ciudad Conduccion");
        ventana.alPresionar(this::alPresionar);
        entrada = new Entrada(ventana.id());
        render = new Renderizador();
        escena = new DibujoEscena(render, mapa);
        minimapa = new Minimapa(render);
        hud = new Hud();
        sonidos = new Sonidos();
    }

    private void bucle() {
        double anterior = ventana.tiempo();
        int cuadros = 0;
        int maximoCuadros = Integer.getInteger("demo.frames", 0);
        while (!ventana.debeCerrar()) {
            ventana.procesarEventos();
            double actual = ventana.tiempo();
            float deltaTime = (float) Math.min(actual - anterior, DELTA_MAXIMO);
            anterior = actual;
            actualizar(deltaTime);
            ventana.actualizarTamano();
            if (!ventana.minimizada()) {
                dibujarCuadro();
            }
            ventana.presentar();
            cuadros++;
            if (maximoCuadros > 0 && cuadros >= maximoCuadros) {
                ventana.cerrar();
            }
        }
    }

    private void limpiar() {
        if (sonidos != null) {
            sonidos.cerrar();
        }
        if (hud != null) {
            hud.eliminar();
        }
        if (render != null) {
            render.eliminar();
        }
        if (ventana != null) {
            ventana.destruir();
        }
    }

    // ==================== Controles ====================

    private void alPresionar(int tecla) {
        switch (tecla) {
            case GLFW_KEY_ESCAPE -> ventana.cerrar();
            case GLFW_KEY_ENTER, GLFW_KEY_KP_ENTER -> {
                if (estado == EstadoJuego.INICIO) {
                    estado = EstadoJuego.JUGANDO;
                    sonidos.efecto(Sonidos.INICIO);
                }
            }
            case GLFW_KEY_P -> {
                if (estado == EstadoJuego.JUGANDO) {
                    estado = EstadoJuego.PAUSA;
                    sonidos.pausarMusica(true);
                    sonidos.efecto(Sonidos.CLICK);
                } else if (estado == EstadoJuego.PAUSA) {
                    estado = EstadoJuego.JUGANDO;
                    sonidos.pausarMusica(false);
                    sonidos.efecto(Sonidos.CLICK);
                }
            }
            case GLFW_KEY_R -> reiniciar();
            case GLFW_KEY_C -> { camara.alternar(); sonidos.efecto(Sonidos.CLICK); }
            case GLFW_KEY_N -> { iluminacion.alternarNoche(); sonidos.efecto(Sonidos.CLICK); }
            case GLFW_KEY_F -> { iluminacion.alternarFaros(); sonidos.efecto(Sonidos.CLICK); }
            case GLFW_KEY_M -> { mostrarMapa = !mostrarMapa; sonidos.efecto(Sonidos.CLICK); }
            case GLFW_KEY_H -> { mostrarAyuda = !mostrarAyuda; sonidos.efecto(Sonidos.CLICK); }
            case GLFW_KEY_Q -> sonidos.efecto(Sonidos.CLAXON);
            default -> {
            }
        }
    }

    /** R: auto, entregas, cronómetro y tráfico vuelven al inicio para un recorrido nuevo. */
    private void reiniciar() {
        boolean veniaDeVictoria = estado == EstadoJuego.VICTORIA;
        auto.reiniciar();
        entregas.reiniciar();
        trafico.reiniciar();
        tiempoAviso = 0;
        frenoSonando = false;
        estado = EstadoJuego.JUGANDO;
        if (veniaDeVictoria) {
            sonidos.reiniciarMusica();
        } else {
            sonidos.pausarMusica(false);
        }
        sonidos.efecto(Sonidos.CLICK);
    }

    // ==================== Actualización ====================

    private void actualizar(float deltaTime) {
        if (estado == EstadoJuego.PAUSA) {
            return;
        }
        tiempoMundo += deltaTime;
        tiempoAviso = Math.max(0, tiempoAviso - deltaTime);
        esperaChoque = Math.max(0, esperaChoque - deltaTime);
        if (estado == EstadoJuego.INICIO) {
            camara.girarOrbita(deltaTime * 0.15f);
            trafico.actualizar(deltaTime, auto);
            return;
        }
        ControlesAuto controles = entrada.controlesAuto();
        boolean choque = auto.actualizar(deltaTime, controles, via);
        if (choque && esperaChoque == 0) {
            float delanteX = auto.x() + auto.frenteX() * (Auto.RADIO + 0.5f);
            float delanteZ = auto.z() + auto.frenteZ() * (Auto.RADIO + 0.5f);
            boolean contraAuto = !trafico.permite(auto.x(), auto.z(), delanteX, delanteZ, Auto.RADIO)
                || trafico.toca(chata.x(), chata.z(), Chata.RADIO + 0.5f);
            sonidos.efecto(contraAuto ? Sonidos.CHOQUE_AUTO : Sonidos.CHOQUE_EDIFICIO);
            esperaChoque = PAUSA_ENTRE_CHOQUES;
        }
        boolean frenando = controles.freno() && Math.abs(auto.velocidad()) > 2f;
        if (frenando && !frenoSonando) {
            sonidos.efecto(Sonidos.FRENO);
        }
        frenoSonando = frenando;
        trafico.actualizar(deltaTime, auto);
        if (entregas.actualizar(deltaTime, auto.x(), auto.z(), auto.velocidad())) {
            if (entregas.completo()) {
                estado = EstadoJuego.VICTORIA;
                sonidos.pausarMusica(true);
                sonidos.efecto(Sonidos.VICTORIA);
            } else {
                aviso = "ENTREGA " + entregas.completadas() + "/" + entregas.total() + " COMPLETADA";
                tiempoAviso = DURACION_AVISO;
                sonidos.efecto(Sonidos.ENTREGA);
            }
        }
        tiempoTitulo -= deltaTime;
        if (tiempoTitulo <= 0) {
            actualizarTitulo();
            tiempoTitulo = INTERVALO_TITULO;
        }
    }

    /** Indicador básico en la barra de título (además del HUD). */
    private void actualizarTitulo() {
        String titulo = "Ciudad Conduccion | " + auto.kilometrosPorHora() + " km/h"
            + " | " + (iluminacion.esNoche() ? "Noche" : "Dia")
            + " | Faros " + (iluminacion.farosEncendidos() ? "ON" : "OFF")
            + " | Entregas " + entregas.completadas() + "/" + entregas.total()
            + " | " + (int) entregas.tiempo() + " s"
            + " | " + mapa.sectorEn(auto.x(), auto.z()).nombre();
        if (estado == EstadoJuego.VICTORIA) {
            titulo += " | GANASTE! R: jugar otra vez";
        }
        ventana.titulo(titulo);
    }

    // ==================== Dibujo ====================

    private void dibujarCuadro() {
        int ancho = ventana.ancho();
        int alto = ventana.alto();
        glViewport(0, 0, ancho, alto);
        if (iluminacion.esNoche()) {
            glClearColor(0.03f, 0.05f, 0.10f, 1);
        } else {
            glClearColor(0.53f, 0.72f, 0.90f, 1);
        }
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        render.comenzar();
        render.setModoMapa(false);
        camara.aplicar(render.programa(), auto, mapa, ancho, alto, estado == EstadoJuego.INICIO);
        escena.luces().enviar(iluminacion, auto);
        float[] destino = entregas.destinoActual();
        Runnable dibujarCiudad = () -> escena.dibujar(auto, iluminacion, trafico, destino, semaforos, tiempoMundo);
        dibujarCiudad.run();
        if (mostrarMapa) {
            minimapa.dibujar(ancho, alto, mapa.limite() + 2, dibujarCiudad, auto, destino, trafico.vehiculos());
        }
        dibujarInterfaz(ancho, alto);

        int error = glGetError();
        if (error != GL_NO_ERROR) {
            throw new IllegalStateException("Error OpenGL al dibujar: " + error);
        }
    }

    private void dibujarInterfaz(int ancho, int alto) {
        hud.comenzar(ancho, alto);
        if (estado != EstadoJuego.INICIO) {
            hud.dibujarPanel(datosHud());
        }
        if (mostrarAyuda) {
            hud.dibujarAyuda();
        }
        if (tiempoAviso > 0) {
            hud.dibujarAviso(aviso);
        }
        switch (estado) {
            case INICIO -> menu.dibujarInicio(hud, entregas.total());
            case PAUSA -> menu.dibujarPausa(hud);
            case VICTORIA -> menu.dibujarVictoria(hud, (int) entregas.tiempo());
            default -> {
            }
        }
        hud.terminar();
    }

    private DatosHud datosHud() {
        return new DatosHud(auto.kilometrosPorHora(), iluminacion.farosEncendidos(), iluminacion.esNoche(),
            camara.modo().nombre(), entregas.completadas(), entregas.total(), (int) entregas.tiempo(),
            mapa.sectorEn(auto.x(), auto.z()).nombre(), entrada.pulsada(GLFW_KEY_Q), mostrarMapa);
    }
}
