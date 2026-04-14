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

    @FXML private GridPane gridButacas;
    @FXML private ComboBox<String> comboTipo;
    @FXML private ComboBox<String> comboEvento;
    @FXML private Label mensajeNombreUsuario;
    @FXML public TextArea txtAreaTicket;
    @FXML public TextField TxfbuscarEntrada;
    @FXML public Button btnComprar;
    @FXML public Button buscarTicket;

    private static final int FILAS    = 10;
    private static final int COLUMNAS = 10;
    private final Button[][] botones  = new Button[FILAS][COLUMNAS];

    // Instancia activa para que ClienteVPN pueda refrescar la UI
    private static VentasDeAsientosController instanciaActiva;

    private static final int LIBRE      = 0;
    private static final int SELECCIONADA = 1;
    private static final int RESERVADA  = 2;

    public double precioTotal;
    public int    cantidadAsientos;
    private Evento eventoActual;

    @FXML
    public void initialize() throws IOException {
        this.auditorio = App.auditorio;
        instanciaActiva = this;

        // Evento activo al iniciar
        eventoActual = auditorio.getEventoActual();

        // Mostrar nombre del cliente
        Cliente cliente = auditorio.getClienteActual();
        if (cliente != null) {
            mensajeNombreUsuario.setText(cliente.getNombreUsuario());
        }

        // Crear la cuadrícula de butacas
        gridButacas.setDisable(false);
        crearButacas();

        // Tipos de entrada
        comboTipo.getItems().setAll("GENERAL", "VIP", "ESTUDIANTIL");

        // ── FIX: poblar comboEvento desde TODOS los eventos (no solo cartelera) ──
        // La lista "cartelera" nunca se persiste, así que usamos getArregloEventos()
        actualizarListaEventos();

        // Listener del combo de eventos
        comboEvento.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String nombreSelec = comboEvento.getValue();
                if (nombreSelec == null) return;

                for (Evento evento : auditorio.getArregloEventos()) {
                    if (evento.getNombre().equals(nombreSelec)) {
                        eventoActual = evento;
                        auditorio.setEventoActual(evento);
                        // Refrescar colores de butacas
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
    }

    // ==================== BUTACAS ====================

    @FXML
    public void crearButacas() {
        gridButacas.getChildren().clear();
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                Button btn = new Button();
                btn.setPrefSize(34, 25);
                btn.getStyleClass().add("seat-button");
                botones[i][j] = btn;

                final int fila = i;
                final int col  = j;
                btn.setOnAction(ev -> manejarClickMesa(fila, col));
                gridButacas.add(btn, j, i);
                actualizarColor(i, j);
            }
        }
    }

    /**
     * Llamado por ClienteVPN (desde Platform.runLater) cuando llega
     * una actualización de red.
     */
    public static void refrescarBotones() {
        if (instanciaActiva != null) {
            // Sincronizar eventoActual con el que tiene el auditorio
            instanciaActiva.eventoActual = instanciaActiva.auditorio.getEventoActual();
            // Actualizar lista de eventos en combo
            instanciaActiva.actualizarListaEventos();
        }
    }

    /**
     * Actualiza el comboEvento con todos los eventos del auditorio.
     * Selecciona el evento activo automáticamente.
     */
    private void actualizarListaEventos() {
        // Preserved selection (event in room takes priority)
        String seleccionado = eventoActual != null
                ? eventoActual.getNombre()
                : comboEvento.getValue();

        comboEvento.getItems().clear();
        for (Evento ev : auditorio.getArregloEventos()) {
            comboEvento.getItems().add(ev.getNombre());
        }

        if (seleccionado != null && comboEvento.getItems().contains(seleccionado)) {
            comboEvento.getSelectionModel().select(seleccionado);
        } else if (!comboEvento.getItems().isEmpty()) {
            comboEvento.getSelectionModel().selectFirst();
            String nombre = comboEvento.getSelectionModel().getSelectedItem();
            for (Evento ev : auditorio.getArregloEventos()) {
                if (ev.getNombre().equals(nombre)) {
                    eventoActual = ev;
                    break;
                }
            }
        }

        // Refrescar colores con el nuevo eventoActual
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                actualizarColor(i, j);
            }
        }
    }

    private void manejarClickMesa(int fila, int col) {
        if (eventoActual == null) {
            Alerta.mostrar("Sin evento", "No hay evento seleccionado", Alert.AlertType.WARNING);
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
                Alerta.mostrar("Asiento ocupado",
                        "Este asiento ya fue vendido.", Alert.AlertType.WARNING);
                return;
        }
        actualizarColor(fila, col);
    }

    private void actualizarColor(int fila, int col) {
        Button btn = botones[fila][col];
        if (btn == null) return;
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

    // ==================== COMPRA ====================

    @FXML
    public void coordinadorDeBoton() throws IOException {
        creacionDeEntradasConElComboBox();
    }

    @FXML
    public void creacionDeEntradasConElComboBox() {
        if (eventoActual == null) {
            Alerta.mostrar("Sin evento", "Seleccione un evento antes de comprar",
                    Alert.AlertType.ERROR);
            return;
        }

        String tipoSeleccionado = comboTipo.getValue();
        if (tipoSeleccionado == null) {
            Alerta.mostrar("Aviso", "Seleccione un tipo de entrada", Alert.AlertType.WARNING);
            return;
        }

        String nombre = mensajeNombreUsuario.getText();
        cantidadAsientos = contarAsientosSeleccionados();
        if (cantidadAsientos == 0) {
            Alerta.mostrar("Aviso", "Seleccione al menos un asiento",
                    Alert.AlertType.WARNING);
            return;
        }

        String numeroAsiento = identificadorDeButacas();
        Entrada nuevaEntrada;

        try {
            switch (tipoSeleccionado) {
                case "VIP":
                    nuevaEntrada = new EntradaVip(nombre, eventoActual, 0,
                            cantidadAsientos, numeroAsiento);
                    break;
                case "ESTUDIANTIL":
                    nuevaEntrada = new EntradaEstudiante(nombre, eventoActual, 0,
                            cantidadAsientos, numeroAsiento);
                    break;
                default:
                    nuevaEntrada = new EntradaGeneral(nombre, eventoActual, 0,
                            cantidadAsientos, numeroAsiento);
                    break;
            }

            double precioCalculado = nuevaEntrada.calcularPrecio();
            nuevaEntrada.setPrecioFinalCalculado(precioCalculado);

            // Asociar entrada al cliente y al evento
            Cliente cliente = auditorio.getClienteActual();
            cliente.agregarEntrada(nuevaEntrada);
            eventoActual.agregarEntrada(nuevaEntrada);

            // Marcar asientos como RESERVADOS y notificar por red
            for (int i = 0; i < FILAS; i++) {
                for (int j = 0; j < COLUMNAS; j++) {
                    if (eventoActual.getMatrizAsientos()[i][j] == SELECCIONADA) {
                        eventoActual.getMatrizAsientos()[i][j] = RESERVADA;
                        actualizarColor(i, j);
                        App.red.enviarMensaje("RESERVAR_ASIENTO:"
                                + eventoActual.getNombre() + "," + i + "," + j);
                    }
                }
            }

            // Mostrar ticket en pantalla
            txtAreaTicket.setText(nuevaEntrada.generarTicket());

            // Exportar a TXT
            Persistencia.exportarTicketATxt(nuevaEntrada);

            Alerta.mostrar("¡Compra exitosa!",
                    "Entrada generada y guardada correctamente.\n"
                    + "Precio pagado: ₡" + String.format("%.2f", precioCalculado),
                    Alert.AlertType.INFORMATION);

            App.red.enviarMensaje("ACTUALIZAR_TODO");

        } catch (Exception e) {
            Alerta.mostrar("Error", "Ocurrió un error al procesar la compra: "
                    + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public String identificadorDeButacas() {
        if (eventoActual == null) return "";
        StringBuilder sb = new StringBuilder("Asientos seleccionados:\n");
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (eventoActual.getMatrizAsientos()[i][j] == SELECCIONADA) {
                    sb.append("Fila: ").append(i + 1)
                      .append(" Col: ").append(j + 1).append("\n");
                }
            }
        }
        return sb.toString();
    }

    @FXML
    private int contarAsientosSeleccionados() {
        if (eventoActual == null) return 0;
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

    // ==================== BÚSQUEDA ====================

    @FXML
    public void buscarEntrada() {
        String input = TxfbuscarEntrada.getText().trim();
        if (input.isEmpty()) {
            Alerta.mostrar("Error", "Ingrese ID o nombre de cliente",
                    Alert.AlertType.WARNING);
            return;
        }

        try {
            // Búsqueda por ID numérico
            int idBuscado = Integer.parseInt(input);
            for (Cliente cliente : auditorio.getClientes()) {
                for (Entrada entrada : cliente.getEntradas()) {
                    if (entrada.getIdEntrada() == idBuscado) {
                        txtAreaTicket.setText(entrada.generarTicket());
                        return;
                    }
                }
            }
        } catch (NumberFormatException e) {
            // Búsqueda por nombre de cliente
            StringBuilder resultados = new StringBuilder(
                    "Tickets de " + input + ":\n\n");
            boolean encontrado = false;

            for (Cliente cliente : auditorio.getClientes()) {
                if (cliente.getNombreUsuario().equalsIgnoreCase(input)) {
                    for (Entrada entrada : cliente.getEntradas()) {
                        resultados.append(entrada.generarTicket())
                                  .append("\n---\n");
                        encontrado = true;
                    }
                }
            }
            if (encontrado) {
                txtAreaTicket.setText(resultados.toString());
                return;
            }
        }

        Alerta.mostrar("No encontrado",
                "No existe entrada con ese ID o nombre de cliente",
                Alert.AlertType.INFORMATION);
    }

    @FXML
    public void CerrarSesion() throws IOException {
        auditorio.cargarCliente(null); // Limpiar sesión
        instanciaActiva = null;
        App.setRoot("AccesoClienteVista");
    }
}
