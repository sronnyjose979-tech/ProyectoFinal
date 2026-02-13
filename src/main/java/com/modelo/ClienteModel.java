package com.modelo;

import java.util.ArrayList;

public class ClienteModel {

    private static String nombreUsuario;
    private static int contra;

    private static ArrayList<String> nombre = new ArrayList<>();

    public ClienteModel(String nombreUsuario, int contra) {
        ClienteModel.nombreUsuario = nombreUsuario;
        this.contra = contra;
        if (!nombre.contains(nombreUsuario)) {
            nombre.add(nombreUsuario);
        }
    }

    public void agregarNombreALista(String nuevoNombre) {
        ClienteModel.nombre.add(nuevoNombre);
    }

    public ArrayList<String> getListaNombres() {
        return nombre;
    }

    public static String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        ClienteModel.nombreUsuario = nombreUsuario;
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
