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
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import UtilsAlertas.mostrarAlerta;
import static UtilsAlertas.mostrarAlerta.mostrarAlerta;
import com.modelo.Auditorio;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.auditorio = App.auditorio;

        // TODO
    }

    @FXML
    private void btnIniciarSesion(ActionEvent e) throws IOException {
        if (auditorio.getClientes().isEmpty()) {
            mostrarAlerta("Erorr", "No hay cliente registrado", Alert.AlertType.WARNING);
            return;
        }
        boolean clienteEncontrado = false;

        for (ClienteModel cliente : auditorio.getClientes()) {

            if ((txtIniciarNombre.getText().equals(cliente.getNombreUsuario())) && (txtIniciarContra.getText().equals(cliente.getContra()))) {
                auditorio.cargarCliente(cliente);
                clienteEncontrado = true;
                App.setRoot("VistaVentaAsientos");
                break;

            }

        }
        if (!clienteEncontrado) {
            mostrarAlerta("Error", "Usuario o Contraseña Incorrecta", Alert.AlertType.WARNING);

        }
    }

    @FXML
    public void btnRegistrarUsuario() throws NoHayUsuarioException, IOException {
//        try {

        String nombreAValidar = txtRegistarUsuario.getText().trim();
        String contraEscrita = txtContraseñaNueva.getText().trim();

        if (nombreAValidar.trim().isEmpty() || contraEscrita.isEmpty()) {
            mostrarAlerta("Error", "Por favor rellene todos los campos", Alert.AlertType.WARNING);
            return;
        } else {
            ClienteModel cliente = new ClienteModel(nombreAValidar, contraEscrita);
            auditorio.agregarCliente(cliente);
            auditorio.cargarCliente(cliente);
            txtRegistarUsuario.setText("");
            txtContraseñaNueva.setText("");

            mostrarAlerta("Exito", "UsuarioRegistrado", Alert.AlertType.INFORMATION);
        }
//            if (!cliente.getListaNombres().contains(nombreAValidar) && cliente.getContra() != contra) {
//                cliente.setNombreUsuario(nombreAValidar);
//                cliente.setContra(contra);
//                
//                throw new NoHayUsuarioException("Debe registrar su usuario!");
//            }
//            if (cliente.getContra() != contra) {
//                mostrarAlerta("Error", "Contra incorrecta", Alert.AlertType.ERROR);
//            }
//        } catch (NoHayUsuarioException e) {
//            mostrarAlerta("Aviso", e.getMessage(), Alert.AlertType.INFORMATION);
//        } catch (NumberFormatException e) {
//            mostrarAlerta("Error", "La contraseña debe contener numeros", Alert.AlertType.ERROR);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @FXML
    private void btnngresarAdmin() throws IOException {
        App.setRoot("AdminLogin");

    }

    

}
