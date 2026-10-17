#version 330 core
// Iluminación local: ambiente + sol direccional + farolas puntuales + dos faros tipo foco.
// No hay sombras: una luz puede atravesar un edificio.

#define MAX_FAROLAS 16

in vec3 vMundo;
in vec3 vNormal;

uniform vec3 uColor;                    // material (color base) de la caja
uniform vec3 uFarolas[MAX_FAROLAS];     // posiciones de las bombillas
uniform int uNumFarolas;                // cantidad de farolas usadas del arreglo
uniform vec3 uAuto;                     // centro del auto a la altura de los faros
uniform vec3 uFrente;                   // dirección frontal del auto (unitaria, en XZ)
uniform int uNoche;                     // 1 = noche, 0 = día
uniform int uFaros;                     // 1 = faros encendidos
uniform int uEmision;                   // 1 = objeto que emite: se ve con su color, pero NO ilumina a otros
uniform int uMapa;                      // 1 = minimapa con colores planos

out vec4 color;

void main() {
    if (uEmision == 1 || uMapa == 1) {
        color = vec4(uColor, 1.0);
        return;
    }

    vec3 normal = normalize(vNormal);

    // Ambiente y sol direccional (Lambert: max(dot(N, L), 0)).
    vec3 luz = vec3(0.48);
    float intensidadSol = 0.65;
    if (uNoche == 1) {
        luz = vec3(0.12, 0.16, 0.24);
        intensidadSol = 0.10;
    }
    vec3 direccionSol = normalize(vec3(0.4, 1.0, 0.3));
    luz += vec3(intensidadSol) * max(dot(normal, direccionSol), 0.0);

    // Farolas: luz puntual con difusa de Lambert y atenuación cuadrática con la distancia.
    if (uNoche == 1) {
        for (int i = 0; i < MAX_FAROLAS; i++) {
            if (i >= uNumFarolas) {
                break;
            }
            vec3 haciaLuz = uFarolas[i] - vMundo;
            float distancia = length(haciaLuz);
            float difusa = max(dot(normal, normalize(haciaLuz)), 0.0);
            float atenuacion = 1.0 + 0.12 * distancia + 0.045 * distancia * distancia;
            luz += vec3(1.0, 0.73, 0.34) * difusa * 3.0 / atenuacion;
        }
    }

    // Faros: dos focos que siguen al auto; cono suave y alcance limitado.
    if (uFaros == 1) {
        vec3 lateral = vec3(-uFrente.z, 0.0, uFrente.x);   // lado derecho del auto
        vec3 eje = normalize(uFrente + vec3(0.0, -0.10, 0.0)); // inclinado hacia el suelo
        for (int i = 0; i < 2; i++) {
            float separacion = (i == 0) ? -0.55 : 0.55;
            vec3 origen = uAuto + uFrente * 1.36 + lateral * separacion;
            vec3 haciaSuperficie = vMundo - origen;
            float distancia = length(haciaSuperficie);
            vec3 direccion = haciaSuperficie / max(distancia, 0.001);
            float cono = smoothstep(0.85, 0.97, dot(direccion, eje));
            float difusa = max(dot(normal, -direccion), 0.0);
            float atenuacion = 1.0 + 0.04 * distancia * distancia;
            float alcance = 1.0 - smoothstep(22.0, 32.0, distancia);
            luz += vec3(1.0, 0.94, 0.72) * cono * difusa * alcance * 8.0 / atenuacion;
        }
    }

    color = vec4(uColor * luz, 1.0);
}
