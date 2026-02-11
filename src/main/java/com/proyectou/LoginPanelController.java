/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.proyectou;

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

public class LoginPanelController implements Initializable {

    private ClienteModel cliente = new ClienteModel("", 0);
    @FXML
    private TextField txtNombre;
    @FXML
    private PasswordField txtContra;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void btnUsuatioInvitado(ActionEvent e) throws IOException {

        App.setRoot("VistaVentaAsientos");

    }

    @FXML
    private void btnngresarAdmin() throws IOException {
        App.setRoot("adminPanel");
    }

    @FXML
    public void registrarYValidarUsuarioEnElLogin() throws NoHayUsuarioException, IOException {
        try {

            String nombreAValidar = txtNombre.getText();
            String contraEscrita = txtContra.getText();

            if (nombreAValidar.trim().isEmpty() || contraEscrita.isEmpty()) {
                mostrarAlerta("Error", "Por favor rellene todos los campos", Alert.AlertType.WARNING);
                return;
            }
            int contra = Integer.parseInt(contraEscrita);

            if (!cliente.getListaNombres().contains(nombreAValidar) && cliente.getContra() != contra) {
                cliente.setNombreUsuario(nombreAValidar);
                cliente.setContra(contra);
                throw new NoHayUsuarioException("Debe registrar su usuario!");
            }
            if (cliente.getContra() != contra) {
                mostrarAlerta("Error", "Contra incorrecta", Alert.AlertType.ERROR);
            }

            //SI LO ENCUENTRA DEJA ENTRAR
            App.setRoot("VistaVentaAsientos");

        } catch (NoHayUsuarioException e) {
            mostrarAlerta("Aviso", e.getMessage(), Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "La contraseña debe contener numeros", Alert.AlertType.NONE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
