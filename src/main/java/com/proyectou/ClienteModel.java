package com.proyectou;

import java.util.ArrayList;

public class ClienteModel {

    private static String nombreUsuario;
    private int contra;

    private ArrayList<String> nombre = new ArrayList<>();

    public ClienteModel(String nombreUsuario, int contra) {
        this.nombreUsuario = nombreUsuario;
        this.contra=contra;
        this.nombre.add(nombreUsuario);
    }

    public void agregarNombreALista(String nuevoNombre) {
        this.nombre.add(nuevoNombre);
    }

    public ArrayList<String> getListaNombres() {
        return nombre;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int getContra() {
        return contra;
    }

    public void setContra(int contra) {
        this.contra = contra;
    }

    public boolean comprobacion(String nombreUsuario, int contra) {

        if (this.nombreUsuario.equals(nombreUsuario) && this.contra == contra) {
            return true;
        }

        return false;
    }
}
