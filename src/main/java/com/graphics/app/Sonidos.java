package com.graphics.app;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Música y efectos en WAV PCM, con el audio que trae Java (sin librerías nuevas).
 * Si el equipo no tiene salida de audio, el juego sigue en silencio.
 */
public class Sonidos {

    public static final String MUSICA = "/sounds/afterStar.wav";
    public static final String CHOQUE_AUTO = "/sounds/choqueCar.wav";
    public static final String CHOQUE_EDIFICIO = "/sounds/choqueEdificio.wav";
    public static final String CLICK = "/sounds/click.wav";
    public static final String ENTREGA = "/sounds/eachEntrega.wav";
    public static final String FRENO = "/sounds/frenar.wav";
    public static final String CLAXON = "/sounds/horn.wav";
    public static final String INICIO = "/sounds/start.wav";
    public static final String VICTORIA = "/sounds/victory.wav";

    private static final float VOLUMEN_MUSICA = -14f;

    private final Map<String, Clip> efectos = new HashMap<>();
    private Clip musica;

    public Sonidos() {
        musica = abrir(MUSICA);
        if (musica != null) {
            volumen(musica, VOLUMEN_MUSICA);
            musica.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /** Reproduce un efecto desde el principio. Si ya sonaba, lo reinicia. */
    public void efecto(String ruta) {
        Clip clip = efectos.get(ruta);
        if (clip == null) {
            clip = abrir(ruta);
            if (clip == null) {
                return;
            }
            efectos.put(ruta, clip);
        }
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    /** Detiene la música en pausa o al ganar, y la retoma al continuar. */
    public void pausarMusica(boolean pausa) {
        if (musica == null) {
            return;
        }
        if (pausa) {
            musica.stop();
        } else if (!musica.isRunning()) {
            musica.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /** Vuelve a empezar la música en bucle (después de la victoria). */
    public void reiniciarMusica() {
        if (musica == null) {
            return;
        }
        musica.stop();
        musica.setFramePosition(0);
        musica.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void cerrar() {
        for (Clip clip : efectos.values()) {
            clip.close();
        }
        if (musica != null) {
            musica.close();
        }
    }

    private static Clip abrir(String ruta) {
        try (InputStream entrada = Sonidos.class.getResourceAsStream(ruta)) {
            if (entrada == null) {
                return null;
            }
            try (AudioInputStream audio = AudioSystem.getAudioInputStream(new BufferedInputStream(entrada))) {
                Clip clip = AudioSystem.getClip();
                clip.open(audio);
                return clip;
            }
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            return null;
        }
    }

    private static void volumen(Clip clip, float decibelios) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float valor = Math.max(control.getMinimum(), Math.min(control.getMaximum(), decibelios));
            control.setValue(valor);
        }
    }
}
