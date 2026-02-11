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

public class LoginPanelController implements Initializable {

    private ClienteModel cliente;
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
    public void registrarUsuarioEnElLogin() {
        String nombreEscrito = txtNombre.getText();
        
        if(!nombreEscrito.trim().isEmpty()){
            
        }

    }

    @FXML
    public void comprobacionSiElUsuarioEstaRegistrado() throws NoHayUsuarioException, IOException {
        String nombreAValidar = txtNombre.getText();
        String passwordEscrita = txtContra.getText();
        int contra = Integer.parseInt(passwordEscrita);

        try {
            //!cliente.getListaNombres().contains(nombreAValidar)
            if (!cliente.getListaNombres().contains(nombreAValidar) && cliente.getContra() != contra) {
                throw new NoHayUsuarioException("Debe registrar su usuario!");
            }
            //SI LO ENCUENTRA DEJA ENTRAR
            App.setRoot("VistaVentaAsientos");

        } catch (NoHayUsuarioException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mensaje de error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
    }
}
