package com.modelo;

public class Auditorio {

    private boolean[][] asientos;

    public Auditorio() {
        asientos = new boolean[10][10]; // matriz obligatoria del proyecto
    }

    public void ocuparAsiento(int fila, int columna) {
        asientos[fila][columna] = true;
    }

    public boolean estaOcupado(int fila, int columna) {
        return asientos[fila][columna];
    }

    public void reiniciarAsientos() {
        for (int i = 0; i < asientos.length; i++) {
            for (int j = 0; j < asientos[i].length; j++) {
                asientos[i][j] = false;
            }
        }
    }
}
