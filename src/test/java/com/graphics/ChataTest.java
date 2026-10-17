package com.graphics;

import com.graphics.mundo.Colisiones;
import com.graphics.mundo.MapaCiudad;
import com.graphics.vehiculo.Auto;
import com.graphics.vehiculo.Chata;
import com.graphics.vehiculo.ControlesAuto;

import junit.framework.TestCase;

/** Chata: posición inicial, barra rígida al girar y choque que detiene al auto. */
public class ChataTest extends TestCase {

    private final MapaCiudad mapa = new MapaCiudad();
    private final Colisiones colisiones = new Colisiones(mapa);

    public void testEmpiezaRectaDetrasDelAuto() {
        Auto auto = new Auto(new Chata());
        Chata chata = auto.chata();
        assertEquals(Auto.INICIO_X, chata.x(), 1e-4f);
        assertEquals(Auto.INICIO_Z + Chata.DISTANCIA_ENGANCHE + Chata.LARGO_LANZA, chata.z(), 1e-4f);
        assertEquals(auto.angulo(), chata.angulo(), 0f);
        assertTrue(colisiones.puedeUbicar(chata.forma()));
    }

    /** Al girar, la chata va por detrás del auto, a distancia fija del enganche y sin doblarse de más. */
    public void testAlGirarLaBarraNoSeEstiraYLaChataSeRetrasa() {
        Auto auto = new Auto(new Chata());
        ControlesAuto acelerarIzquierda = new ControlesAuto(1, 1, false);
        for (int i = 0; i < 60; i++) {
            auto.actualizar(1 / 60f, acelerarIzquierda, colisiones);
        }
        Chata chata = auto.chata();
        float engancheX = auto.x() - auto.frenteX() * Chata.DISTANCIA_ENGANCHE;
        float engancheZ = auto.z() - auto.frenteZ() * Chata.DISTANCIA_ENGANCHE;
        float largo = (float) Math.hypot(chata.x() - engancheX, chata.z() - engancheZ);
        assertEquals(Chata.LARGO_LANZA, largo, 1e-3f);
        float diferencia = auto.angulo() - chata.angulo();
        assertTrue("la chata debe girar menos que el auto", diferencia > 0.01f);
        assertTrue(diferencia <= Chata.ANGULO_MAXIMO + 1e-4f);
    }

    /** Retrocediendo hacia el borde sur, la chata toca primero y el auto se detiene aunque él sí cabría. */
    public void testSiLaChataChocaElAutoSeDetiene() {
        Auto auto = new Auto(new Chata());
        auto.colocar(Auto.INICIO_X, 46, 0, 0);
        ControlesAuto retroceder = new ControlesAuto(-1, 0, false);
        for (int i = 0; i < 600; i++) {
            auto.actualizar(1 / 60f, retroceder, colisiones);
        }
        Chata chata = auto.chata();
        assertEquals(0f, auto.velocidad(), 0f);
        assertTrue(colisiones.puedeUbicar(chata.forma()));
        // La chata queda pegada al borde y el auto, que solo no habría parado, se detiene antes.
        assertEquals(mapa.limite(), chata.z() + Chata.MEDIO_LARGO, 0.05f);
        assertTrue(auto.z() < mapa.limite() - Auto.MEDIO_LARGO - 1);
    }
}
