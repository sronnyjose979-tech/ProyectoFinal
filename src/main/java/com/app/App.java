package com.app;

import com.modelo.Auditorio;
import com.modelo.Persistencia;
import com.red.ClienteVPN;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.*;

/**
 * Punto de entrada de la app.
 * Lee la IP del servidor desde "config.properties" en el directorio de trabajo.
 * Si el archivo no existe, lo crea con "localhost" como valor por defecto.
 */
public class App extends Application {

    public static Auditorio  auditorio = new Auditorio();
    public static ClienteVPN red       = new ClienteVPN();

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Cargar datos persistidos
        Persistencia.cargarDatos(auditorio);

        // Leer IP y puerto desde config.properties
        String ip    = leerPropiedad("servidor.ip",    "localhost");
        int    puerto = Integer.parseInt(leerPropiedad("servidor.puerto", "5000"));

        System.out.println("[App] Conectando a servidor: " + ip + ":" + puerto);
        red.conectar(ip, puerto, auditorio);

        scene = new Scene(loadFXML("AccesoClienteVista"), 950, 700);
        stage.setScene(scene);
        stage.setTitle("Sistema de Gestión de Auditorio");
        stage.setResizable(true);
        stage.show();
    }

    @Override
    public void stop() {
        Persistencia.guardarDatos(auditorio);
        System.out.println("[App] Aplicación finalizada. Datos guardados.");
    }

    // ==================== NAVEGACIÓN ====================

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/com/vista/" + fxml + ".fxml"));
        return loader.load();
    }

    // ==================== CONFIG ====================

    /**
     * Lee una propiedad del archivo config.properties.
     * Si el archivo o la clave no existen, devuelve el valor por defecto
     * y crea el archivo con los valores predeterminados.
     */
    private static String leerPropiedad(String clave, String valorDefault) {
        File config = new File("config.properties");

        // Crear archivo con valores por defecto si no existe
        if (!config.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(config))) {
                pw.println("# ================================================");
                pw.println("# Configuracion de red del Sistema de Auditorio");
                pw.println("# Edita este archivo para cambiar el servidor.");
                pw.println("# ================================================");
                pw.println("servidor.ip=localhost");
                pw.println("servidor.puerto=5000");
                System.out.println("[App] Creado config.properties con valores por defecto.");
            } catch (IOException e) {
                System.err.println("[App] No se pudo crear config.properties: "
                        + e.getMessage());
            }
            return valorDefault;
        }

        // Leer propiedad del archivo
        try (BufferedReader br = new BufferedReader(new FileReader(config))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(clave + "=")) {
                    return line.substring(clave.length() + 1).trim();
                }
            }
        } catch (IOException e) {
            System.err.println("[App] Error leyendo config.properties: " + e.getMessage());
        }

        return valorDefault;
    }

    public static void main(String[] args) {
        launch();
    }
}
