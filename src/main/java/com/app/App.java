package com.app;

import com.modelo.Auditorio;
import com.modelo.Persistencia;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    // Instancia única (Singleton simplificado) accesible globalmente
    public static Auditorio auditorio = new Auditorio();

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Cargar datos persistidos
        Persistencia.cargarDatos(auditorio);

        // Ruta a los FXML en la nueva estructura com/vista
        scene = new Scene(loadFXML("LoginPanel"), 950, 700);

        stage.setScene(scene);
        stage.setTitle("Sistema de Gestión de Auditorio");

        // Agregar icono si se desea (opcional)
        // stage.getIcons().add(new Image(App.class.getResourceAsStream("/icon.png")));

        stage.show();
    }

    /**
     * Persistencia al cerrar la aplicación
     */
    @Override
    public void stop() {
        Persistencia.guardarDatos(auditorio);
        System.out.println("[App] Aplicación finalizada. Datos guardados.");
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        // Ruta absoluta a la carpeta vista
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/vista/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
