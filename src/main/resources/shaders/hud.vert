#version 330 core
// Dibuja un rectángulo 2D expresado en píxeles (origen arriba a la izquierda).

layout (location = 0) in vec2 aPos;   // esquina del quad unitario (0..1)

uniform vec4 uRect;       // x, y, ancho, alto en píxeles
uniform vec2 uPantalla;   // tamaño del framebuffer en píxeles

void main() {
    vec2 pixel = uRect.xy + aPos * uRect.zw;
    // Píxeles -> coordenadas normalizadas; Y se invierte porque OpenGL mide desde abajo.
    vec2 ndc = vec2(pixel.x / uPantalla.x * 2.0 - 1.0, 1.0 - pixel.y / uPantalla.y * 2.0);
    gl_Position = vec4(ndc, 0.0, 1.0);
}
