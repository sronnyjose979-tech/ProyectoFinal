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
    private TextField txtRegistarUsuario;
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
        this.auditorio = App.auditorio;
        this.red = App.red;
    }

    @FXML
    void btnIngresarAdmin(ActionEvent event) {
        try {
            App.setRoot("AccesoAdministradorVista");
        } catch (IOException e) {
            Alerta.mostrar("Error", "No se pudo cargar la vista de admin", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * BUG CORREGIDO: Antes usaba validarLogin() (boolean) y nunca guardaba
     * el objeto Cliente en sesión. Ahora usa autenticarCliente() para obtener
     * la instancia y la pasa al auditorio con cargarCliente().
     */
    @FXML
    void btnIniciarSesion(ActionEvent event) {
        String user = txtUsuarioIniciarSesion.getText().trim();
        String pass = txtContrasenaIniciarSesion.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            Alerta.mostrar("Error", "Complete usuario y contraseña", Alert.AlertType.WARNING);
            return;
        }

        Cliente cliente = auditorio.autenticarCliente(user, pass);
        if (cliente != null) {
            // Establecer el cliente en sesión (fix crítico)
            auditorio.cargarCliente(cliente);
            try {
                App.setRoot("VistaVentaAsientos");
            } catch (IOException e) {
                Alerta.mostrar("Error", "No se pudo abrir la sala", Alert.AlertType.ERROR);
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

        // 2. Sincronizar por red
        red.enviarMensaje("NUEVO_CLIENTE:" + nombre + "," + contra);

        // 3. Limpiar y avisar
        txtRegistarUsuario.clear();
        txtRegistrarContrasena.clear();
        Alerta.mostrar("Éxito", "Usuario registrado y sincronizado en la red", Alert.AlertType.INFORMATION);
    }
}