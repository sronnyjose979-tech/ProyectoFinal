package com.proyectou;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class ButacasController {

    @FXML
    private ComboBox<String> comboTipo;

    @FXML
    public void initialize() {
        comboTipo.getItems().addAll(
                "GENERAL",
                "VIP",
                "ESTUDIANTE"
        );

        /* private void switchToSecondary() throws IOException {
        App.setRoot("VistaButacas");
    }*/
    }
}
