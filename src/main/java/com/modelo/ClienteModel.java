package com.modelo;

import java.util.ArrayList;

public class ClienteModel {

    private String nombreUsuario;
    private String contra;
    private ArrayList<Entrada> Entradas;

    public ClienteModel(String nombreUsuario, String contra) {
        this.nombreUsuario = nombreUsuario;
        this.contra = contra;
        Entradas = new ArrayList<>();
    }

    public void agregarEntrada(Entrada entrada) {
        Entradas.add(entrada);
    }

    public ArrayList<Entrada> getEntradas() {
        return Entradas;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContra() {
        return contra;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }

}
