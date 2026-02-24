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

    public static Auditorio auditorio = new Auditorio();

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        Persistencia.cargarDatos(auditorio);

        scene = new Scene(loadFXML("AccesoClienteVista"), 950, 700);

        stage.setScene(scene);
        stage.setTitle("Sistema de Gestión de Auditorio");

        
        stage.show();
    }


    @Override
    public void stop() {
        Persistencia.guardarDatos(auditorio);
        System.out.println("[App] Aplicación finalizada. Datos guardados.");
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/vista/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
