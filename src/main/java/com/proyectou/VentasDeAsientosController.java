package com.proyectou;

import java.io.IOException;
import java.time.Duration;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class VentasDeAsientosController {

    @FXML
    private GridPane gridButacas;

    @FXML
    private ComboBox<String> comboTipo;

    @FXML
    private Label mensaje;

    @FXML
    private TextField mensajeNombreUsuario;

    private static final int FILAS = 10;
    private static final int COLUMNAS = 10;

    private Button[][] botones = new Button[FILAS][COLUMNAS];
    private int[][] estados = new int[FILAS][COLUMNAS];

    private int filaSeleccionada = -1;
    private int colSeleccionada = -1;

    private static final int LIBRE = 0;
    private static final int SELECCIONADA = 1;
    private static final int RESERVADA = 2;

    @FXML
    public void initialize() throws IOException {
        String nombreGuardado = ClienteModel.getNombreUsuario();
        mensajeNombreUsuario.setText(nombreGuardado);
        gridButacas.setDisable(false);
        crearMesas();

        comboTipo.getItems().addAll(
                "GENERAL",
                "VIP",
                "ESTUDIANTE"
        );

        // App.setRoot("VistaButacas");
    }

    @FXML
    public void crearMesas() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                Button btn = new Button();
                btn.setPrefSize(34, 25);
                botones[i][j] = btn;
                estados[i][j] = LIBRE;

                final int fila = i;
                final int col = j;

                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        manejarClickMesa(fila, col);
                    }
                });

                gridButacas.add(btn, j, i);
                actualizarColor(i, j);
            }
        }

    }

    // MANEJO DE CLICK
    private void manejarClickMesa(int fila, int col) {

        int estado = estados[fila][col];

        switch (estado) {
            case LIBRE:
                estados[fila][col] = SELECCIONADA;
                filaSeleccionada = fila;
                colSeleccionada = col;
                break;

            case SELECCIONADA:
                estados[fila][col] = LIBRE;
                filaSeleccionada = -1;
                colSeleccionada = -1;
                break;

            case RESERVADA:
                estados[fila][col] = LIBRE; //SI LO DEJO ME PERMITE QUITAR LA RESERVA
                filaSeleccionada = -1;
                colSeleccionada = -1;
                return;
        }

        actualizarColor(fila, col);
    }

    // COLORES SEGÚN ESTADO
    private void actualizarColor(int fila, int col) {

        Button btn = botones[fila][col];

        switch (estados[fila][col]) {
            case LIBRE:
                btn.setStyle("-fx-background-color: #0000FF;");
                break;

            case SELECCIONADA:
                btn.setStyle("-fx-background-color: #4CAF50;");
                break;

            case RESERVADA:
                btn.setStyle("-fx-background-color: #FF2A00;");
                btn.setDisable(true);
                break;
        }
    }

    @FXML
    private void reservacion() throws IOException {
        boolean huboSeleccion = false;
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (estados[i][j] == SELECCIONADA) {
                    estados[i][j] = RESERVADA;
                    actualizarColor(i, j);
                    huboSeleccion = true;
                }
            }

        }

        if (filaSeleccionada == -1 || colSeleccionada == -1) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("No hay mesa seleccionada");
            alert.showAndWait();
            return;
        }

        estados[filaSeleccionada][colSeleccionada] = RESERVADA;
        actualizarColor(filaSeleccionada, colSeleccionada);

        filaSeleccionada = -1;
        colSeleccionada = -1;
    }

}
