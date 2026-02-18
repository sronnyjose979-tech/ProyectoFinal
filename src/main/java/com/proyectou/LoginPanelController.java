/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.proyectou;

import com.modelo.ClienteModel;
import UtilsAlertas.NoHayUsuarioException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import static UtilsAlertas.mostrarAlerta.mostrarAlerta;
import com.modelo.Auditorio;
import javafx.scene.control.TabPane;

public class LoginPanelController implements Initializable {

    Auditorio auditorio;

    @FXML
    private PasswordField txtContraseñaNueva;

    @FXML
    private PasswordField txtIniciarContra;

    @FXML
    private TextField txtIniciarNombre;

    @FXML
    private TextField txtRegistarUsuario;

    @FXML
    private TabPane tabLogin;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.auditorio = App.auditorio;

        // TODO
    }

    @FXML
    private void btnIniciarSesion(ActionEvent e) throws IOException {
        if (txtIniciarNombre.getText().trim().isEmpty() || txtIniciarContra.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "No pueden quedar espacios en blanco", Alert.AlertType.ERROR);
            return;
        }
        String nombre = txtIniciarNombre.getText().trim();
        String contra = txtIniciarContra.getText().trim();
        ClienteModel cliente = auditorio.autenticarCliente(nombre, contra);
        if (cliente != null) {
            auditorio.cargarCliente(cliente);
            mostrarAlerta("Éxito", "Bienvenido " + cliente.getNombreUsuario(), Alert.AlertType.INFORMATION);
            App.setRoot("VistaVentaAsientos");
        } else {
            mostrarAlerta("Error", "Usuario o Contraseña Incorrectos", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void btnRegistrarUsuario() throws NoHayUsuarioException, IOException {

        if (txtRegistarUsuario.getText().trim().isEmpty() || txtContraseñaNueva.getText().isEmpty()) {
            mostrarAlerta("Error", "Por favor rellene todos los campos", Alert.AlertType.ERROR);
            return;
        }

        String nombreAValidar = txtRegistarUsuario.getText().trim();
        String contraEscrita = txtContraseñaNueva.getText().trim();

        auditorio.usuarioExiste(nombreAValidar);
        if (auditorio.usuarioExiste(nombreAValidar)) {
            mostrarAlerta("Error", "El nombre de usuario ya está en uso. Intente con otro.", Alert.AlertType.ERROR);
            return;
        }

        ClienteModel cliente = new ClienteModel(nombreAValidar, contraEscrita);
        auditorio.agregarCliente(cliente);
        auditorio.cargarCliente(cliente);
        txtRegistarUsuario.setText("");
        txtContraseñaNueva.setText("");

        mostrarAlerta("Éxito", "Usuario registrado correctamente. ¡Ya puedes iniciar sesión!", Alert.AlertType.INFORMATION);
        tabLogin.getSelectionModel().select(0);
    }

    @FXML
    private void btnngresarAdmin() throws IOException {
        App.setRoot("AdminLogin");

    }

}
