package com.controlador;

import com.app.App;
import com.modelo.Auditorio;
import com.modelo.Cliente;
import com.red.ClienteVPN;
import com.util.Alerta;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AccesoClienteVistaController {

    @FXML
    private TextField txtRegistarUsuario; // Sin la 'r' para que coincida con tu FXML
    @FXML
    private PasswordField txtRegistrarContrasena;
    @FXML
    private TextField txtUsuarioIniciarSesion;
    @FXML
    private PasswordField txtContrasenaIniciarSesion;

    private Auditorio auditorio;
    private ClienteVPN red;

    @FXML
    public void initialize() {
        this.auditorio = new Auditorio();
        // Inicializar la red
        red = new ClienteVPN();
        red.conectar("100.112.172.27", 5000, auditorio);
    }

    // ESTE ES EL MÉTODO QUE FALTABA Y DABA EL ERROR
    @FXML
    void btnIngresarAdmin(ActionEvent event) {
        try {
            System.out.println("Cambiando a vista de administrador...");
            App.setRoot("AccesoAdministradorVista"); // Asegúrate que el nombre del FXML sea correcto
        } catch (IOException e) {
            Alerta.mostrar("Error", "No se pudo cargar la vista de admin", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    void btnIniciarSesion(ActionEvent event) {
        String user = txtUsuarioIniciarSesion.getText();
        String pass = txtContrasenaIniciarSesion.getText();
        
        if (auditorio.validarLogin(user, pass)) {
            try {
                App.setRoot("VistaVentaAsientos");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alerta.mostrar("Error", "Usuario o contraseña incorrectos", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void btnRegistrarUsuario(ActionEvent e) {
        String nombre = txtRegistarUsuario.getText().trim();
        String contra = txtRegistrarContrasena.getText().trim();

        if (nombre.isEmpty() || contra.isEmpty()) {
            Alerta.mostrar("Error", "Complete todos los campos", Alert.AlertType.WARNING);
            return;
        }

        if (auditorio.usuarioExiste(nombre)) {
            Alerta.mostrar("Error", "El usuario ya existe", Alert.AlertType.WARNING);
            return;
        }

        // 1. Guardar localmente
        Cliente nuevo = new Cliente(nombre, contra);
        auditorio.agregarCliente(nuevo);

        // 2. ENVIAR POR RED
        red.enviarMensaje("NUEVO_CLIENTE:" + nombre + "," + contra);

        // 3. Limpiar y avisar
        txtRegistarUsuario.clear();
        txtRegistrarContrasena.clear();
        Alerta.mostrar("Éxito", "Usuario registrado y sincronizado", Alert.AlertType.INFORMATION);
    }
}