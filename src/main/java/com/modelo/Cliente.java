package com.modelo;

import java.util.ArrayList;

public class Cliente {

    private String nombreCliente;
    private String contraseñaCliente;
    private ArrayList<Entrada> Entradas;

    public Cliente(String nombreCliente, String contraseñaCliente) {
        this.nombreCliente = nombreCliente;
        this.contraseñaCliente = contraseñaCliente;
        Entradas = new ArrayList<>();
    }

    public void agregarEntrada(Entrada entrada) {
        Entradas.add(entrada);
    }

    public ArrayList<Entrada> getEntradas() {
        return Entradas;
    }

    public String getNombreUsuario() {
        return nombreCliente;
    }

    public void setNombreUsuario(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getContra() {
        return contraseñaCliente;
    }

    public void setContra(String contraseñaCliente) {
        this.contraseñaCliente = contraseñaCliente;
    }

}
