
package com.modelo;


public class Administrador {

    private String nombreAdministrador;
    private String contraseñaAdmin;

    public Administrador(String nombreAdministrador, String contraseñaAdmin) {

        this.nombreAdministrador = nombreAdministrador;
        this.contraseñaAdmin = contraseñaAdmin;
    }

    public String getNombreAdministrador() {
        return nombreAdministrador;
    }

    public void setNombreAdministrador(String nombreAdministrador) {
        this.nombreAdministrador = nombreAdministrador;
    }

    public String getContraseñaAdmin() {
        return contraseñaAdmin;
    }

    public void setContraseñaAdmin(String contraseñaAdmin) {
        this.contraseñaAdmin = contraseñaAdmin;
    }

}
