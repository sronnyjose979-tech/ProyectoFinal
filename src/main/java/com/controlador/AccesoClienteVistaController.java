package com.controlador;

import com.app.App;
import com.modelo.Auditorio;
import com.modelo.Cliente;
import com.red.ClienteVPN;
import com.util.Alerta;
import com.util.NoHayUsuarioException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TabPane;

public class AccesoClienteVistaController implements Initializable {

    Auditorio auditorio;

    @FXML
    private PasswordField txtRegistrarContrasena;

    @FXML
    private TextField txtRegistarUsuario;

    @FXML
    private PasswordField txtContrasenaIniciarSesion;

    @FXML
    private TextField txtUsuarioIniciarSesion;

    @FXML
    private TabPane tabLogin;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.auditorio = App.auditorio;
        // Dentro del método de un botón "Conectar"
        ClienteVPN red = new ClienteVPN();
// USA AQUÍ LA IP QUE SALÍA EN TU CAPTURA DE TAILSCALE
        red.conectar("100.100.84.120", 5000);
        red.enviarMensaje("Hola desde el cliente de " + System.getProperty("user.name"));
    }

    @FXML
    public void btnRegistrarUsuario(ActionEvent e) {

        if (txtRegistarUsuario.getText().trim().isEmpty() || txtRegistrarContrasena.getText().isEmpty()) {
            Alerta.mostrar("Error", "Por favor rellene todos los campos", Alert.AlertType.ERROR);
            return;
        }

        String nombreARegistrar = txtRegistarUsuario.getText().trim();
        String contraARegistrar = txtRegistrarContrasena.getText().trim();

        try {
            if (auditorio.usuarioExiste(nombreARegistrar)) {
                throw new NoHayUsuarioException("El nombre de usuario '" + nombreARegistrar + "' ya está en uso.");
            }

            Cliente cliente = new Cliente(nombreARegistrar, contraARegistrar);
            auditorio.agregarCliente(cliente);
            auditorio.cargarCliente(cliente);
            txtRegistarUsuario.setText("");
            txtRegistrarContrasena.setText("");

            Alerta.mostrar("Éxito", "Usuario registrado correctamente.",
                    Alert.AlertType.INFORMATION);
            tabLogin.getSelectionModel().select(0);

        } catch (NoHayUsuarioException ex) {
            Alerta.mostrar("Error", "El nombre de usuario ya está en uso. Intente con otro.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void btnIniciarSesion(ActionEvent e) throws IOException {
        if (txtUsuarioIniciarSesion.getText().trim().isEmpty() || txtContrasenaIniciarSesion.getText().trim().isEmpty()) {
            Alerta.mostrar("Error", "No pueden quedar espacios en blanco", Alert.AlertType.ERROR);
            return;
        }
        String nombreUsuario = txtUsuarioIniciarSesion.getText().trim();
        String contrasenaUsuario = txtContrasenaIniciarSesion.getText().trim();
        Cliente cliente = auditorio.autenticarCliente(nombreUsuario, contrasenaUsuario);
        if (cliente != null) {
            auditorio.cargarCliente(cliente);
            Alerta.mostrar("Éxito", "Bienvenido " + cliente.getNombreUsuario(), Alert.AlertType.INFORMATION);
            App.setRoot("VistaVentaAsientos");
        } else {
            Alerta.mostrar("Error", "Usuario o Contraseña Incorrectos", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void btnngresarAdmin() throws IOException {
        App.setRoot("AccesoAdministradorVista");
    }
}
