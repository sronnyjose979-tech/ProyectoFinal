package com.proyectou;

import static UtilsAlertas.mostrarAlerta.mostrarAlerta;
import com.modelo.Auditorio;
import com.modelo.ClienteModel;
import com.modelo.Entrada;
import com.modelo.EntradaEstudiante;
import com.modelo.EntradaGeneral;
import com.modelo.EntradaVip;
import com.modelo.Evento;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class VentasDeAsientosController {

    Auditorio auditorio;

    @FXML
    private GridPane gridButacas;

    @FXML
    private ComboBox<String> comboTipo;

    @FXML
    private Label mensaje;

    @FXML
    private TextField mensajeNombreUsuario;

    @FXML
    public TextArea txtAreaTicket;

    @FXML
    public Button btnComprar;

    private static final int FILAS = 10;
    private static final int COLUMNAS = 10;

    private static Button[][] botones = new Button[FILAS][COLUMNAS];
    private static int[][] estados = new int[FILAS][COLUMNAS];

    private int filaSeleccionada = -1;
    private int colSeleccionada = -1;

    private static final int LIBRE = 0;
    private static final int SELECCIONADA = 1;
    private static final int RESERVADA = 2;

    public static double precioTotal;

    public static int cantidadAsientos;

    @FXML
    public void initialize() throws IOException {
        this.auditorio = App.auditorio;

        ClienteModel cliente = auditorio.getClienteActual();
        String nombreGuardado = cliente.getNombreUsuario();
        mensajeNombreUsuario.setText(nombreGuardado);
        gridButacas.setDisable(false);
        crearMesas();

        comboTipo.getItems().addAll(
                "GENERAL",
                "VIP",
                "ESTUDIANTE"
        );

    }

    @FXML
    public void crearMesas() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                Button btn = new Button();
                btn.setPrefSize(34, 25);
                
                // --- CAMBIO DE ESTILO (SOLO ESTO SE AGREGÓ) ---
                btn.getStyleClass().add("seat-button"); 
                // ----------------------------------------------

                botones[i][j] = btn;
                //estados[i][j] = LIBRE;

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

    public static void reiniciarButacas() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                estados[i][j] = LIBRE;
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

    private void actualizarColor(int fila, int col) {

        Button btn = botones[fila][col];

        // --- CAMBIO DE ESTILO (Reemplaza los setStyle anteriores) ---
        // 1. Limpiar clases viejas para que no se acumulen
        btn.getStyleClass().removeAll("seat-free", "seat-selected", "seat-reserved");
        // 2. Quitar cualquier estilo manual residual
        btn.setStyle(null);

        switch (estados[fila][col]) {
            case LIBRE:
                btn.getStyleClass().add("seat-free");
                btn.setDisable(false);
                break;

            case SELECCIONADA:
                btn.getStyleClass().add("seat-selected");
                btn.setDisable(false);
                break;

            case RESERVADA:
                btn.getStyleClass().add("seat-reserved");
                btn.setDisable(true);
                btn.setOpacity(1.0); // Para que se vea el color rojo aunque esté deshabilitado
                break;
        }
        // ------------------------------------------------------------
    }

    @FXML
    private void reservacion() throws IOException {

        String tipoSeleccionado = comboTipo.getValue();

        if (tipoSeleccionado == null) {
            mostrarAlerta("Aviso", "Debe seleccionar un tipo de entrada!", Alert.AlertType.WARNING);
            return;
        }
        if (filaSeleccionada == -1 || colSeleccionada == -1) {
            mostrarAlerta("Aviso", " No hay mesas seleccionadas", Alert.AlertType.WARNING);
            return;
        }

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
        estados[filaSeleccionada][colSeleccionada] = RESERVADA;
        actualizarColor(filaSeleccionada, colSeleccionada);

        filaSeleccionada = -1;
        colSeleccionada = -1;
    }

    @FXML
    public void CerrarSesion() throws IOException {
        App.setRoot("LoginPanel");

    }

    @FXML
    public static String identificadorDeButacas() {

        StringBuilder asientosTexto = new StringBuilder("Asientos reservados:\n");

        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (estados[i][j] == RESERVADA) {
                    asientosTexto.append("Fila: ").append(i + 1).append(" Col: ").append(j + 1).append("\n");
                }

            }

        }
        return asientosTexto.toString();
    }

    public void guardarEstadosEnArchivo() {

    }

    @FXML
    public void coordinadorDeBoton() throws IOException {

        creacionDeEntradasConElComboBox();
        guardarEstadosEnArchivo();

    }

    public double verElPrecio() {

        Evento eventoActual = auditorio.getEvento();
        String tipo = comboTipo.getValue();
        double precioBase = eventoActual.getPrecioBase();
        double subtotal = precioBase * cantidadAsientos;

        if (tipo == null) {
            return subtotal;
        }

        switch (tipo) {
            case "VIP":
                precioTotal = subtotal * 1.50; //CON AUMENTO
                break;
            case "ESTUDIANTE":
                precioTotal = subtotal * 0.80; // CON DESCUENTO
                break;
            default:
                precioTotal = subtotal; //NORMAL
                break;
        }
        return precioTotal;
    }

    @FXML
    public void creacionDeEntradasConElComboBox() {

        if (auditorio.getEvento() == null) {
            Evento evento = auditorio.getEventoActual();

            mostrarAlerta("Aviso", "El auditorio no tenía evento.", Alert.AlertType.WARNING);
            //auditorio.crearEventoPorDefecto();
        }

        String tipoSeleccionado = comboTipo.getValue();
        String nombre = mensajeNombreUsuario.getText();
        Evento eventoActual = auditorio.getEvento();

        if (tipoSeleccionado == null) {
            return;
        }
        cantidadAsientos = contarAsientosSeleccionados();

        if (cantidadAsientos == 0) {
            mostrarAlerta("Aviso", "No ha seleccionado ningún asiento", Alert.AlertType.WARNING);
            return;
        }

        double total = verElPrecio();
        String detalle = identificadorDeButacas();
        Entrada nuevaEntrada;

        switch (tipoSeleccionado) {
            case "VIP":
                nuevaEntrada = new EntradaVip(nombre, eventoActual, precioTotal, cantidadAsientos, detalle);
                break;
            case "ESTUDIANTE":
                nuevaEntrada = new EntradaEstudiante(nombre, eventoActual, precioTotal, cantidadAsientos, detalle);
                break;
            default:
                nuevaEntrada = new EntradaGeneral(nombre, eventoActual, precioTotal, cantidadAsientos, detalle);
                break;
        }

        txtAreaTicket.setText(nuevaEntrada.generarTicket());
    }

    private int contarAsientosSeleccionados() {
        int contador = 0;
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (estados[i][j] == RESERVADA) {
                    contador++;
                }
            }
        }
        return contador;
    }

}