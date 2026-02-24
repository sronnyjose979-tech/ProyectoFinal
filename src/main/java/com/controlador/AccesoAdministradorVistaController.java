package com.controlador;

import com.app.App;
import com.modelo.Administrador;
import com.modelo.Auditorio;
import com.util.Alerta;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;

public class AccesoAdministradorVistaController implements Initializable {
    

    Auditorio auditorioA;

    @FXML
    private PasswordField txtnombreAdmin;
    @FXML
    private PasswordField txtContraseña;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.auditorioA = App.auditorio;
    }

    @FXML
    private void btnIniciarSesion() throws IOException {
        try {
            Administrador admin = auditorioA.cargarAdmin();

            String nombreAdmin = txtnombreAdmin.getText();
            String contraseñaAdmin = txtContraseña.getText();

            if (nombreAdmin.trim().isEmpty() || contraseñaAdmin.trim().isEmpty()) {
                Alerta.mostrar("Error", "No puede quedar un espacio en blanco", Alert.AlertType.WARNING);
                return;
            }
            if (admin != null && admin.getNombreAdministrador().equals(nombreAdmin)
                    && admin.getContraseñaAdmin().equals(contraseñaAdmin)) {
                Alerta.mostrar("Exito", "Iniciando sesión", Alert.AlertType.INFORMATION);
                App.setRoot("AdminPanel"); 
            } else {
                Alerta.mostrar("Error", "Nombre o contraseña incorrectos", Alert.AlertType.ERROR);
            }
        } catch (NullPointerException e) {
            Alerta.mostrar("Error", "No hay administrador registrado, contacte con soporte", Alert.AlertType.ERROR);
        }
    }

    public void btnregresarInicio() throws IOException {
        App.setRoot("AccesoClienteVista");
    }
}
