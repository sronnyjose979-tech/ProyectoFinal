package com.modelo;

import java.util.ArrayList;

public class ClienteModel {// solo va a ser un modelo de cliente, se va controlar mediante el auditorio que va registrar el cliente

    private String nombreUsuario;
    private String contra;
    private ArrayList<Entrada> Entradas;

    public ClienteModel(String nombreUsuario, String contra) {
        this.nombreUsuario = nombreUsuario;
        this.contra = contra;
        Entradas = new ArrayList<>();
    }

        public void agregarEntrada(Entrada entrada) {// se va agregar el cliente para poder usarse en todas las clases
        Entradas.add(entrada);
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

//    public boolean comprobacion(String nombreUsuario, int contra) {
//
//        if (this.nombreUsuario.equals(nombreUsuario) && this.contra) {
//            return true;
//        }
//
//        return false;
//    }
}
