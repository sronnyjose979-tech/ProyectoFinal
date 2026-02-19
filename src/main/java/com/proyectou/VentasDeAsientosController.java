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
    private ComboBox<String> comboEvento;

    @FXML
    private Label mensaje;

    @FXML
    private Label mensajeNombreUsuario;

    @FXML
    public TextArea txtAreaTicket;

    @FXML
    public TextField TxfbuscarEntrada;

    @FXML
    public Button btnComprar;

    @FXML
    public Button buscarTicket;

    private static final int FILAS = 10;
    private static final int COLUMNAS = 10;

    private static Button[][] botones = new Button[FILAS][COLUMNAS];

    private int filaSeleccionada = -1;
    private int colSeleccionada = -1;

    private static final int LIBRE = 0;
    private static final int SELECCIONADA = 1;
    private static final int RESERVADA = 2;

    public double precioTotal;
    public int cantidadAsientos;

    private Evento eventoActual;

    @FXML
    public void initialize() throws IOException {
        this.auditorio = App.auditorio;

        eventoActual = auditorio.eventoActual;

        ClienteModel cliente = auditorio.getClienteActual();
        String nombreGuardado = cliente.getNombreUsuario();
        mensajeNombreUsuario.setText(nombreGuardado);
        gridButacas.setDisable(false);
        crearMesas();

        comboTipo.getItems().addAll(
                "GENERAL",
                "VIP",
                "ESTUDIANTE");

        if (auditorio.getEventosEnCartelera() != null && !auditorio.getEventosEnCartelera().isEmpty()) {

            for (int i = 0; i < auditorio.getEventosEnCartelera().size(); i++) {
                Evento evento = auditorio.getEventosEnCartelera().get(i);
                comboEvento.getItems().add(evento.getNombre());
            }

            // this.eventoActual = auditorio.getEvento();
            if (eventoActual != null) {
                comboEvento.getSelectionModel().select(eventoActual.getNombre());
            } else {
                comboEvento.getSelectionModel().selectFirst();
            }
        } else {
            comboEvento.setPromptText("No hay eventos!");
        }

        comboEvento.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String nombreSelec = comboEvento.getValue();
                for (int i = 0; i < auditorio.getEventosEnCartelera().size(); i++) {
                    Evento evento = auditorio.getEventosEnCartelera().get(i);

                    if (evento.getNombre().equals(nombreSelec)) {
                        eventoActual = evento;
                        for (int f = 0; f < FILAS; f++) {
                            for (int c = 0; c < COLUMNAS; c++) {
                                actualizarColor(f, c);
                            }
                        }
                        break;
                    }
                }
            }
        });

        for (int fila = 0; fila < FILAS; fila++) {
            for (int col = 0; col < COLUMNAS; col++) {
                actualizarColor(fila, col);
            }
        }
    }

    @FXML
    public void crearMesas() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                Button btn = new Button();
                btn.setPrefSize(34, 25);

                btn.getStyleClass().add("seat-button");
                // ----------------------------------------------

                botones[i][j] = btn;
                // estados[i][j] = LIBRE;

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

    public void reiniciarButacas() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                eventoActual.getMatrizAsientos()[i][j] = LIBRE;
            }
        }
    }

    // MANEJO DE CLICK
    private void manejarClickMesa(int fila, int col) {
        if (eventoActual == null) {
            mostrarAlerta("error", "no hay evento en sala", Alert.AlertType.WARNING);
            return;
        }

        int estado = eventoActual.getMatrizAsientos()[fila][col];

        switch (estado) {
            case LIBRE:
                eventoActual.getMatrizAsientos()[fila][col] = SELECCIONADA;
                filaSeleccionada = fila;
                colSeleccionada = col;
                break;

            case SELECCIONADA:
                eventoActual.getMatrizAsientos()[fila][col] = LIBRE;
                filaSeleccionada = -1;
                colSeleccionada = -1;
                break;

            case RESERVADA:
                eventoActual.getMatrizAsientos()[fila][col] = LIBRE; 
                filaSeleccionada = -1;
                colSeleccionada = -1;
                return;
        }

        actualizarColor(fila, col);
    }

    private void actualizarColor(int fila, int col) {

        Button btn = botones[fila][col];

     
        btn.getStyleClass().removeAll("seat-free", "seat-selected", "seat-reserved");
        btn.setStyle(null);

        if (eventoActual == null) {
            btn.getStyleClass().add("seat-free"); 
            return;
        }

        switch (eventoActual.getMatrizAsientos()[fila][col]) {
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
    }

    @FXML
    public void CerrarSesion() throws IOException {
        App.setRoot("LoginPanel");

    }

    @FXML
    public String identificadorDeButacas() {
        StringBuilder asientosTexto = new StringBuilder("Asientos seleccionados:\n");
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (eventoActual.getMatrizAsientos()[i][j] == SELECCIONADA) {
                    asientosTexto.append("Fila: ").append(i + 1).append(" Col: ").append(j + 1).append("\n");
                }
            }
        }
        return asientosTexto.toString();
    }

    @FXML
    public void coordinadorDeBoton() throws IOException {

        creacionDeEntradasConElComboBox();
        // guardarEstadosEnArchivo();

    }

    @FXML
    public void creacionDeEntradasConElComboBox() {

        if (eventoActual == null) {
            mostrarAlerta("No hay función activa",
                    "El administrador debe cargar un evento antes de vender entradas",
                    Alert.AlertType.ERROR);
            return;
        }

        String tipoSeleccionado = comboTipo.getValue();
        String nombre = mensajeNombreUsuario.getText();

        if (tipoSeleccionado == null) {
            mostrarAlerta("Aviso", "Debe seleccionar un tipo de entrada!", Alert.AlertType.WARNING);
            return;
        }

        cantidadAsientos = contarAsientosSeleccionados();

        if (cantidadAsientos == 0) {
            mostrarAlerta("Aviso", "No ha seleccionado ningún asiento", Alert.AlertType.WARNING);
            return;
        }

        String numeroAsiento = identificadorDeButacas();
        Entrada nuevaEntrada;

        switch (tipoSeleccionado) {
            case "VIP":
                nuevaEntrada = new EntradaVip(nombre, eventoActual, 0, cantidadAsientos, numeroAsiento);
                break;
            case "ESTUDIANTE":
                nuevaEntrada = new EntradaEstudiante(nombre, eventoActual, 0, cantidadAsientos, numeroAsiento);
                break;
            default:
                nuevaEntrada = new EntradaGeneral(nombre, eventoActual, 0, cantidadAsientos, numeroAsiento);
                break;
        }

        double precioCalculado = nuevaEntrada.calcularPrecio();
        nuevaEntrada.setPrecioFinalCalculado(precioCalculado);

        ClienteModel cliente = auditorio.getClienteActual();
        cliente.agregarEntrada(nuevaEntrada);
        eventoActual.agregarEntrada(nuevaEntrada);

        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (eventoActual.getMatrizAsientos()[i][j] == SELECCIONADA) {
                    eventoActual.getMatrizAsientos()[i][j] = RESERVADA;
                    actualizarColor(i, j);
                }
            }
        }

        txtAreaTicket.setText(nuevaEntrada.generarTicket());

        filaSeleccionada = -1;
        colSeleccionada = -1;
    }

    @FXML
    private int contarAsientosSeleccionados() {
        int contador = 0;
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (eventoActual.getMatrizAsientos()[i][j] == SELECCIONADA) {
                    contador++;
                }
            }
        }
        return contador;
    }

    public void buscarEntrada() {
        String idButacas = TxfbuscarEntrada.getText();

        if (idButacas.isEmpty()) {
            mostrarAlerta("Error", "Ingrese una ID valida", Alert.AlertType.WARNING);
            return;
        }

        int idBuscado;
        try {
            idBuscado = Integer.parseInt(idButacas);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El ID debe ser un numero", Alert.AlertType.ERROR);
            return;
        }

        for (ClienteModel cliente : auditorio.getClientes()) {
            for (int i = 0; i < cliente.getEntradas().size(); i++) {
                Entrada entrada = cliente.getEntradas().get(i);
                if (entrada.getIdEntrada() == idBuscado) {
                    txtAreaTicket.setText(entrada.generarTicket());
                    return;
                }
            }
        }

        mostrarAlerta("No encontrado", "No existe una entrada con ese ID", Alert.AlertType.INFORMATION);

    }

}
