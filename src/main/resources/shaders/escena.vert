#version 330 core
// Transforma el cubo unitario (escala, giro en Y, traslación) y lo proyecta.

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;

uniform vec3 uPos;          // centro de la caja en el mundo
uniform vec3 uEscala;       // tamaño de la caja en cada eje
uniform float uGiro;        // giro alrededor de Y, en radianes
uniform vec3 uOjo;          // posición de la cámara
uniform vec3 uObjetivo;     // punto que observa la cámara
uniform float uAspecto;     // ancho / alto del viewport
uniform float uLejos;       // plano lejano de la perspectiva
uniform float uMitadMapa;   // mitad del lado visible en el minimapa (escala del mapa)
uniform int uMapa;          // 0 = perspectiva, 1 = vista superior ortográfica

out vec3 vMundo;
out vec3 vNormal;

void main() {
    float coseno = cos(uGiro);
    float seno = sin(uGiro);
    // GLSL recibe las columnas: rotación alrededor del eje Y.
    mat3 giro = mat3(
        coseno, 0.0, -seno,
        0.0,    1.0, 0.0,
        seno,   0.0, coseno
    );
    vMundo = giro * (aPos * uEscala) + uPos;
    // Inversa transpuesta de escala y giro: mantiene la normal perpendicular a la cara.
    vNormal = normalize(giro * (aNormal / uEscala));

    if (uMapa == 1) {
        // Proyección ortográfica: norte (-Z) arriba; los objetos altos quedan delante.
        float pantallaX = vMundo.x / uMitadMapa;
        float pantallaY = -vMundo.z / uMitadMapa;
        float profundidad = -vMundo.y / 100.0;
        gl_Position = vec4(pantallaX, pantallaY, profundidad, 1.0);
    } else {
        // Matriz de vista construida con la base de la cámara (derecha, arriba, frente).
        vec3 frente = normalize(uObjetivo - uOjo);
        vec3 derecha = normalize(cross(frente, vec3(0.0, 1.0, 0.0)));
        vec3 arriba = cross(derecha, frente);
        vec3 diferencia = vMundo - uOjo;
        float vistaX = dot(diferencia, derecha);
        float vistaY = dot(diferencia, arriba);
        float vistaZ = -dot(diferencia, frente);
        // Perspectiva con campo visual de 55 grados.
        float factor = 1.0 / tan(radians(55.0) * 0.5);
        float cerca = 0.1;
        float clipZ = (uLejos + cerca) / (cerca - uLejos) * vistaZ + 2.0 * uLejos * cerca / (cerca - uLejos);
        gl_Position = vec4(vistaX * factor / uAspecto, vistaY * factor, clipZ, -vistaZ);
    }
}
