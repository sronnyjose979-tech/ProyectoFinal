package com.controlador;

import com.app.App;
import com.modelo.Auditorio;
import com.modelo.Cliente;
import com.modelo.Entrada;
import com.modelo.EntradaEstudiante;
import com.modelo.EntradaGeneral;
import com.modelo.EntradaVip;
import com.modelo.Evento;
import com.modelo.Persistencia;
import com.util.Alerta;
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
    private Button[][] botones = new Button[FILAS][COLUMNAS];
    private static VentasDeAsientosController instanciaActiva;

    private static final int LIBRE = 0;
    private static final int SELECCIONADA = 1;
    private static final int RESERVADA = 2;

    public double precioTotal;
    public int cantidadAsientos;
    private Evento eventoActual;

    @FXML
    public void initialize() throws IOException {
        this.auditorio = App.auditorio;
        instanciaActiva = this;
        eventoActual = auditorio.getEventoActual();

        Cliente cliente = auditorio.getClienteActual();
        if (cliente != null) {
            mensajeNombreUsuario.setText(cliente.getNombreUsuario());
        }

        gridButacas.setDisable(false);
        crearButacas();

        comboTipo.getItems().setAll("GENERAL", "VIP", "ESTUDIANTIL");

        if (auditorio.getEventosEnCartelera() != null && !auditorio.getEventosEnCartelera().isEmpty()) {

            for (int i = 0; i < auditorio.getEventosEnCartelera().size(); i++) {
                Evento evento = auditorio.getEventosEnCartelera().get(i);
                comboEvento.getItems().add(evento.getNombre());
            }
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
                        auditorio.setEventoActual(evento);

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
    public void crearButacas() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                Button btn = new Button();
                btn.setPrefSize(34, 25);
                btn.getStyleClass().add("seat-button");
                botones[i][j] = btn;

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

    public static void refrescarBotones() {
        if (instanciaActiva != null) {
            for (int i = 0; i < FILAS; i++) {
                for (int j = 0; j < COLUMNAS; j++) {
                    instanciaActiva.actualizarColor(i, j);
                }
            }
        }
    }

    private void manejarClickMesa(int fila, int col) {
        if (eventoActual == null) {
            Alerta.mostrar("Error", "No hay evento en sala", Alert.AlertType.WARNING);
            return;
        }

        int estado = eventoActual.getMatrizAsientos()[fila][col];
        switch (estado) {
            case LIBRE:
                eventoActual.getMatrizAsientos()[fila][col] = SELECCIONADA;
                break;
            case SELECCIONADA:
                eventoActual.getMatrizAsientos()[fila][col] = LIBRE;
                break;
            case RESERVADA:
                Alerta.mostrar("Asiento no disponible", "Este asiento ya fue vendido y no puede modificarse.",
                        Alert.AlertType.WARNING);
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
                btn.setDisable(false);
                btn.setOpacity(1.0);
                break;
        }
    }

    @FXML
    public void CerrarSesion() throws IOException {
        instanciaActiva = null;
        App.setRoot("AccesoClienteVista");
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
    }

    @FXML
    public void creacionDeEntradasConElComboBox() {
        if (eventoActual == null) {
            Alerta.mostrar("No hay función activa", "Seleccione un evento antes de comprar", Alert.AlertType.ERROR);
            return;
        }

        String tipoSeleccionado = comboTipo.getValue();
        String nombre = mensajeNombreUsuario.getText();

        if (tipoSeleccionado == null) {
            Alerta.mostrar("Aviso", "Debe seleccionar un tipo de entrada!", Alert.AlertType.WARNING);
            return;
        }

        cantidadAsientos = contarAsientosSeleccionados();
        if (cantidadAsientos == 0) {
            Alerta.mostrar("Aviso", "No ha seleccionado ningún asiento", Alert.AlertType.WARNING);
            return;
        }

        String numeroAsiento = identificadorDeButacas();
        Entrada nuevaEntrada;

        try {
            switch (tipoSeleccionado) {
                case "VIP":
                    nuevaEntrada = new EntradaVip(nombre, eventoActual, 0, cantidadAsientos, numeroAsiento);
                    break;
                case "ESTUDIANTIL":
                    nuevaEntrada = new EntradaEstudiante(nombre, eventoActual, 0, cantidadAsientos, numeroAsiento);
                    break;
                default:
                    nuevaEntrada = new EntradaGeneral(nombre, eventoActual, 0, cantidadAsientos, numeroAsiento);
                    break;
            }

            double precioCalculado = nuevaEntrada.calcularPrecio();
            nuevaEntrada.setPrecioFinalCalculado(precioCalculado);

            Cliente cliente = auditorio.getClienteActual();
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

            Persistencia.exportarTicketATxt(nuevaEntrada);

            Alerta.mostrar("Compra Exitosa", "Entrada generada correctamente y guardada en TXT.",
                    Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            Alerta.mostrar("Error", "Ocurrió un error al procesar la compra.", Alert.AlertType.ERROR);
        }
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

    @FXML
    public void buscarEntrada() {
        String input = TxfbuscarEntrada.getText().trim();
        if (input.isEmpty()) {
            Alerta.mostrar("Error", "Ingrese ID o nombre de cliente", Alert.AlertType.WARNING);
            return;
        }

        try {
            int idBuscado = Integer.parseInt(input);
            for (int i = 0; i < auditorio.getClientes().size(); i++) {
                Cliente cliente = auditorio.getClientes().get(i);
                for (int j = 0; j < cliente.getEntradas().size(); j++) {
                    Entrada entrada = cliente.getEntradas().get(j);
                    if (entrada.getIdEntrada() == idBuscado) {
                        txtAreaTicket.setText(entrada.generarTicket());
                        return;
                    }
                }
            }

        } catch (NumberFormatException e) {
            StringBuilder resultados = new StringBuilder("Tickets de " + input + ":\n\n");
            boolean encontrado = false;

            for (int i = 0; i < auditorio.getClientes().size(); i++) {
                Cliente cliente = auditorio.getClientes().get(i);
                if (cliente.getNombreUsuario().equalsIgnoreCase(input)) {
                    for (int j = 0; j < cliente.getEntradas().size(); j++) {
                        Entrada entrada = cliente.getEntradas().get(j);
                        resultados.append("ID: ").append(entrada.getIdEntrada())
                                .append(" | Evento: ").append(entrada.getEvento().getNombre())
                                .append("\n");
                        encontrado = true;
                    }
                }
            }

            if (encontrado) {
                txtAreaTicket.setText(resultados.toString());
                return;
            }
        }

        Alerta.mostrar("No encontrado", "No existe entrada con ese ID o Cliente", Alert.AlertType.INFORMATION);
    }
}
