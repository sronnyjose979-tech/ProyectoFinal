/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.proyectou;

import UtilsAlertas.mostrarAlerta;
import com.modelo.Administrador;
import com.modelo.Auditorio;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author sronn
 */
public class AdminLoginController implements Initializable {

    Auditorio auditorioA;

    @FXML
    private PasswordField txtContraseña;

    @FXML
    private TextField txtnombreAdmin;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.auditorioA = App.auditorio;
        // TODO
    }

    @FXML
    private void btnIniciarSesion() throws IOException {
        try {
            Administrador admin = auditorioA.cargarAdmin();

            String nombreAdmin = txtnombreAdmin.getText();
            String contraseñaAdmin = txtContraseña.getText();

            if (nombreAdmin.trim().isEmpty() || contraseñaAdmin.trim().isEmpty()) {
                mostrarAlerta.mostrarAlerta("Error", "No puede quedar un espacio en blanco", Alert.AlertType.WARNING);
                return;
            }
            if (admin != null && admin.getNombreAdministrador().equals(nombreAdmin) && admin.getContraseñaAdmin().equals(contraseñaAdmin)) {
                mostrarAlerta.mostrarAlerta("Exito", "Iniciando sesión", Alert.AlertType.INFORMATION);
                App.setRoot("adminPanel");
            } else {
                mostrarAlerta.mostrarAlerta("Error", "Nombre o contraseña incorrectos", Alert.AlertType.ERROR);
            }
        } catch (NullPointerException e) {
            mostrarAlerta.mostrarAlerta("Error", "No hay administradoFr registrado contacte con soporte", Alert.AlertType.ERROR);

        }
    }

    public void btnregresarInicio() throws IOException {
        App.setRoot("LoginPanel");
    }

}
