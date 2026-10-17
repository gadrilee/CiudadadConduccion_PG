#version 330 core

uniform vec4 uColor;   // color RGBA; alfa < 1 produce paneles semitransparentes

out vec4 color;

void main() {
    color = uColor;
}
