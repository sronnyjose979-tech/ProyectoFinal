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

public class LoginPanelController implements Initializable {

    private ClienteModel cliente = new ClienteModel("", 0);

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
        // TODO
    }
    
    

    @FXML
    private void btnIniciarSesion(ActionEvent e) throws IOException {

        App.setRoot("VistaVentaAsientos");

    }
    
    

    @FXML
    public void btnRegistrarUsuario() throws NoHayUsuarioException, IOException {
        try {

            String nombreAValidar = txtRegistarUsuario.getText();
            String contraEscrita = txtContraseñaNueva.getText();

            if (nombreAValidar.trim().isEmpty() || contraEscrita.isEmpty()) {
                mostrarAlerta("Error", "Por favor rellene todos los campos", Alert.AlertType.WARNING);
                return;
            }
            int contra = Integer.parseInt(contraEscrita);

            if (!cliente.getListaNombres().contains(nombreAValidar) && cliente.getContra() != contra) {
                cliente.setNombreUsuario(nombreAValidar);
                cliente.setContra(contra);
                App.setRoot("VistaVentaAsientos");
                throw new NoHayUsuarioException("Debe registrar su usuario!");
            }
            if (cliente.getContra() != contra) {
                mostrarAlerta("Error", "Contra incorrecta", Alert.AlertType.ERROR);
            }

        } catch (NoHayUsuarioException e) {
            mostrarAlerta("Aviso", e.getMessage(), Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "La contraseña debe contener numeros", Alert.AlertType.ERROR);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void btnngresarAdmin() throws IOException {
        App.setRoot("adminPanel");
    }

}
