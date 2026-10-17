package com.graphics.mundo;

/** Contenido de una celda del mapa. El código numérico es el que se escribe en la matriz. */
public enum TipoCelda {
    CALLE(0),
    EDIFICIO(1),
    PARQUE(2);

    private final int codigo;

    TipoCelda(int codigo) {
        this.codigo = codigo;
    }

    public int codigo() {
        return codigo;
    }

    public static TipoCelda desdeCodigo(int codigo) {
        for (TipoCelda tipo : values()) {
            if (tipo.codigo == codigo) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de celda desconocido: " + codigo);
    }
}
