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

public class AccesoClienteVistaController {

    @FXML
    private TextField txtRegistrarUsuario;
    @FXML
    private PasswordField txtRegistrarContrasena;

    private Auditorio auditorio; // Asumo que se inyecta o se carga antes
    private ClienteVPN red;     // ¡Aquí está la variable que te faltaba!

    @FXML
    public void initialize() {
        // 1. Inicializar auditorio (puedes cargarlo de la persistencia aquí)
        this.auditorio = new Auditorio();

        // 2. Configurar la red
        red = new ClienteVPN();
        // Cambia la IP por la de la máquina que corre el ServidorVPN
        red.conectar("100.112.172.27", 5000, auditorio);
    }

    @FXML
    public void btnRegistrarUsuario(ActionEvent e) {
        // Validaciones de campos vacíos...
        String nombre = txtRegistrarUsuario.getText().trim();
        String contra = txtRegistrarContrasena.getText().trim();

        if (auditorio.usuarioExiste(nombre)) {
            // Mostrar alerta de usuario existente...
            return;
        }

        // 1. Guardar localmente
        Cliente nuevo = new Cliente(nombre, contra);
        auditorio.agregarCliente(nuevo);

        // 2. ENVIAR POR RED (Esto hará que tu compañero lo reciba)
        red.enviarMensaje("NUEVO_CLIENTE:" + nombre + "," + contra);

        // 3. Limpiar y avisar
        txtRegistrarUsuario.clear();
        txtRegistrarContrasena.clear();
        System.out.println("Usuario registrado y enviado a la red.");
    }
}
