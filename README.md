# Ciudad interactiva ampliada con OpenGL

**Autor:** Gabriel Mamani Sandoval

Actividad práctica de programación gráfica: ampliación de la ciudad del proyecto de referencia
(Java 17, Maven, LWJGL 3.3.3, OpenGL 3.3 Core). Toda la geometría se genera con código a partir de un
cubo unitario; no se usan imágenes ni modelos externos.

## Ejecución

Requisitos: JDK 17 o superior, Maven y una tarjeta gráfica con OpenGL 3.3. Desde la carpeta que contiene `pom.xml`:

```sh
mvn compile exec:exec
```

Otros comandos:

```sh
mvn test                                   # pruebas de lógica (no abren ventana)
mvn compile exec:exec -Ddemo.frames=120    # arranque breve: dibuja 120 cuadros y se cierra
```

La primera compilación descarga las dependencias. En macOS el perfil de Maven agrega `-XstartOnFirstThread`.

## Controles

| Tecla | Acción |
|---|---|
| ENTER | Comenzar desde la pantalla de inicio |
| W / S o flechas arriba / abajo | Acelerar / frenar y retroceder |
| A / D o flechas izquierda / derecha | Girar (proporcional a la velocidad) |
| Espacio | Freno |
| Q | Claxon |
| C | Cámara de seguimiento / aérea oblicua |
| N | Día / noche |
| F | Encender / apagar faros |
| M | Mostrar / ocultar minimapa |
| R | Reiniciar auto, entregas, cronómetro y tráfico |
| H | Mostrar / ocultar ayuda de controles |
| P | Pausa |
| ESC | Salir |

**Objetivo:** llegar a las 5 marcas doradas en orden y frenar sobre cada una (a menos de 3 m y a menos de 1 m/s).

## Diseño de la ciudad

Mapa de 11x11 celdas de 10 m (110 x 110 m). Las filas y columnas pares son calles, por lo que todas las
manzanas tienen acceso por las cuatro caras. Hay 25 manzanas: **19 edificios y 6 parques**.

```text
0 0 0 0 0 0 0 0 0 0 0      0 = calle
0 1 0 1 0 2 0 1 0 1 0      1 = edificio
0 0 0 0 0 0 0 0 0 0 0      2 = parque
0 2 0 1 0 1 0 1 0 2 0
0 0 0 0 0 0 0 0 0 0 0      Norte (-Z) arriba
0 1 0 1 0 2 0 1 0 1 0
0 0 0 0 0 0 0 0 0 0 0
0 1 0 2 0 1 0 1 0 1 0
0 0 0 0 0 0 0 0 0 0 0
0 1 0 1 0 1 0 2 0 1 0
0 0 0 0 0 0 0 0 0 0 0
```

La ciudad se divide en cinco sectores con nombre y estilo propio: **Distrito Centro** (rascacielos de
14 a 22 m, tonos azules), **Barrio Norte**, **Puerto Sur**, **Villa Este** y **Lomas Oeste** (edificios de 5 a 12 m
con colores distintos). El sector actual aparece en el HUD y hay un cartel con el nombre de cada sector.

**Coherencia del escenario:** toda la escala se deriva de la matriz en `MapaCiudad` (`limite = filas * 10 / 2`).
El suelo, los límites de circulación, las colisiones, la cámara aérea (radio y altura proporcionales al mapa)
y la escala del minimapa (`uMitadMapa`) leen ese valor. Cambiar la matriz a 13x13 adapta todo automáticamente.

## Funciones implementadas

- **Iluminación** (`escena.frag`): luz ambiental + sol direccional con difusa de Lambert `max(dot(N, L), 0)`
  usando las normales de cada cara. De noche el ambiente es azulado y tenue, el cielo se oscurece y se activan
  **13 farolas** repartidas por todos los sectores, con atenuación `1 + 0.12 d + 0.045 d²`.
  Los **dos faros** siguen la posición y orientación del auto: cono suave (`smoothstep`) y alcance limitado
  (se apagan entre 22 y 32 m).
- **Emisión frente a iluminación:** las bombillas, ventanas encendidas y luces de semáforo usan `uEmision`:
  se ven con su color pero no iluminan nada. La luz que reciben el suelo y los edificios proviene solo del
  cálculo de farolas y faros en el shader.
- **Decoración:** árboles y bancos en los parques, ventanas por piso en las cuatro fachadas (de noche dos de
  cada tres se encienden), pasos peatonales tipo cebra junto a las manzanas, papeleras e hidrantes.
- **Semáforos animados:** 12 semáforos con la secuencia rojo (5 s), verde (5 s) y amarillo (2 s), controlada por
  tiempo y con desfase entre filas de manzanas. Son visuales: no detienen al auto.
- **Minimapa:** segundo pase con viewport y `glScissor` en la esquina superior derecha; proyección
  ortográfica con el norte arriba. Muestra toda la ciudad, el auto (marca cian con punta blanca que indica la
  orientación), el destino activo (dorado) y el tráfico (blanco). Su tamaño se recalcula al redimensionar y
  al terminar se restauran el viewport, el scissor y el modo del shader.
- **Reinicio (R):** restaura posición, orientación y velocidad del auto, las entregas, el cronómetro y el tráfico.
- **Colisiones precisas:** cada vehículo choca con un **rectángulo girado** de su tamaño real
  (auto 2.0 x 2.72 m contando ruedas y faros; chata 1.94 x 2.12 m), y las manzanas son rectángulos de 10 x 10 m.
  Se comprueba con el **teorema del eje separador**: dos rectángulos no se tocan si en alguno de sus 4 ejes
  (los lados de cada uno) sus "sombras" no se solapan. Así el auto puede ir pegado a la acera o pasar al lado
  de otro auto sin espacio invisible. Si un paso se rechaza, también se deshace el giro de ese cuadro.
- **Movimiento por tiempo:** toda la simulación usa `deltaTime` (limitado a 50 ms). Una prueba verifica que
  1 s de aceleración a 30 y a 120 fps produce el mismo recorrido.
- **Indicador en el título:** velocidad, día/noche, faros, entregas, tiempo y sector.
- **Sonido:** música en bucle, arranque, clic de menú, freno, claxon (Q), choque contra auto o edificio,
  entrega y victoria. Los archivos están en `src/main/resources/sounds/` como WAV PCM de 16 bits.

### Mejoras adicionales

- **Tráfico autónomo:** 4 autos recorren rutas cerradas por las calles (anillo exterior, anillo central y dos
  recorridos con giros), siempre por el carril derecho. Giran suavemente en las esquinas, frenan si tienen al
  jugador o a otro auto delante y el jugador no puede atravesarlos.
- **Interfaz:** panel con velocidad (y barra), faros, ambiente, cámara, entregas, tiempo, sector y minimapa;
  ayuda de controles (H); pantalla de inicio, pausa (P), aviso de entrega completada y pantalla de victoria.
  El texto usa una fuente pixelada propia de 5x7 definida en código.
- **Presentación urbana:** sectores con nombre, carteles en la ciudad y mobiliario adicional.
- **Chata (remolque):** el auto arrastra una chata de madera con marco rojo (1.3 x 2 m, un eje y dos ruedas).
  Se modela como una **barra rígida**: el enganche está 1.5 m detrás del centro del auto y la chata se coloca
  siempre a 1.9 m del enganche, mirando hacia él. Por eso en las curvas gira menos que el auto y "corta" la
  esquina por dentro, como un remolque real. El ángulo entre ambos se limita a unos 70° para que no se doble
  sobre el auto. Tiene su propio rectángulo de colisión (1.94 x 2.12 m): si la chata chocaría contra un edificio,
  el borde o un auto, el paso se rechaza y **el auto se detiene**. La carga reduce la aceleración al 80 %,
  sus luces traseras se encienden con los faros, aparece en el minimapa y el tráfico frena si la tiene delante.

## Arquitectura

El código se organiza por responsabilidad. El **modelo no usa OpenGL**, por eso se puede probar sin ventana.

```text
com.graphics
  Main                punto de entrada
  app/                Aplicacion (ciclo de vida y orquestación), Ventana, Entrada, EstadoJuego, Sonidos
  mundo/              MapaCiudad, Manzana, TipoCelda, Sector, Colisiones, Rectangulo, Transitable
  vehiculo/           Auto (física), Chata (remolque), ControlesAuto
  trafico/            Ruta, VehiculoAutonomo, Trafico
  juego/              Entregas
  iluminacion/        Iluminacion, Farola, Semaforo
  render/             ProgramaShader, MallaCubo, MallaQuad, Renderizador, Camara, DibujoEscena,
                      DibujoCiudad, Decoracion, DibujoLuces, DibujoVehiculo, Minimapa
  ui/                 Hud, Menu, FuentePixel, DatosHud
resources/shaders/    escena.vert, escena.frag, hud.vert, hud.frag
```

Cada cuadro: eventos, `deltaTime`, actualización del modelo, escena 3D, minimapa, HUD 2D y presentación.

## Limitaciones

- La iluminación es local: no hay sombras ni oclusión, por lo que una luz puede atravesar un edificio.
- Los semáforos son decorativos: ni el jugador ni el tráfico se detienen en rojo.
- El tráfico sigue rutas fijas. Si dos autos quedan bloqueados entre sí durante más de 3 s, uno ignora al otro
  un momento para evitar un bloqueo permanente (pueden superponerse brevemente).
- Al chocar, el auto se detiene sin rebote ni deslizamiento: para despegarse de una pared hay que retroceder.
  El auto y su chata no chocan entre sí: lo evita el límite de ángulo del enganche.
- No hay peatones ni ruedas animadas. El audio usa WAV PCM de 16 bits con `javax.sound.sampled` (el JDK no reproduce MP3).

## Recursos externos

- Proyecto base del profesor (`clase1` a `clase4`): estructura del cubo con normales, shaders de
  transformación e iluminación, colisión círculo-rectángulo, minimapa con scissor y entregas. Se reorganizó en
  la arquitectura descrita arriba.
- [LWJGL 3.3.3](https://www.lwjgl.org/) (GLFW y OpenGL), descargada por Maven.
- No se usan imágenes, fuentes ni modelos externos.
