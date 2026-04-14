package com.controlador;

import com.app.App;
import com.modelo.Auditorio;
import com.modelo.Entrada;
import com.modelo.Evento;
import com.util.Alerta;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class AdminPanelController implements Initializable {

    @FXML private TableColumn<Evento, String> eventoCol;
    @FXML private TableColumn<Evento, String> fechaCol;
    @FXML private TableColumn<Evento, Double> precioCol;
    @FXML private TableView<Evento> tableEvento;
    @FXML private TextField txtNombreEvento;
    @FXML private TextField txtFechaEvento;
    @FXML private TextField txtPrecioBase;
    @FXML private Label lblEventoActivo;

    private ObservableList<Evento> listaEventos;
    private Auditorio auditorio;
    private Evento eventoActual;

    // Referencia estática para que ClienteVPN pueda refrescar la tabla
    private static AdminPanelController instanciaActiva;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        auditorio = App.auditorio;
        instanciaActiva = this;
        listaEventos = FXCollections.observableArrayList();

        if (auditorio.getArregloEventos() != null) {
            listaEventos.setAll(auditorio.getArregloEventos());
        }

        configurarTabla();
        configurarListenerSeleccion();
        actualizarEtiquetaEventoActivo();
    }

    // ==================== TABLA DE EVENTOS ====================

    private void configurarTabla() {
        eventoCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        fechaCol.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        precioCol.setCellValueFactory(new PropertyValueFactory<>("precioBase"));
        tableEvento.setItems(listaEventos);
    }

    private void configurarListenerSeleccion() {
        tableEvento.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<Evento>() {
                    @Override
                    public void changed(ObservableValue<? extends Evento> obs,
                            Evento oldVal, Evento newVal) {
                        if (newVal != null) {
                            eventoActual = newVal;
                            txtNombreEvento.setText(newVal.getNombre());
                            txtFechaEvento.setText(newVal.getFecha());
                            txtPrecioBase.setText(
                                    String.valueOf(newVal.getPrecioBase()));
                        }
                    }
                });
    }

    private void actualizarEtiquetaEventoActivo() {
        if (auditorio.getEventoActual() != null) {
            lblEventoActivo.setText(
                    "Evento Activo: " + auditorio.getEventoActual().getNombre()
                    + "  —  " + auditorio.getEventoActual().getFecha());
        } else {
            lblEventoActivo.setText("Evento Activo: Ninguno");
        }
    }

    /**
     * Llamado por ClienteVPN cuando llega un nuevo evento o ACTUALIZAR_TODO
     * para refrescar la tabla del panel de admin en tiempo real.
     */
    public static void refrescarTablaEventos() {
        if (instanciaActiva != null) {
            instanciaActiva.listaEventos.setAll(
                    instanciaActiva.auditorio.getArregloEventos());
            instanciaActiva.actualizarEtiquetaEventoActivo();
        }
    }

    // ==================== CRUD DE EVENTOS ====================

    @FXML
    private void crearEvento() {
        if (camposVacios()) {
            Alerta.mostrar("Error", "No pueden quedar espacios en blanco",
                    Alert.AlertType.ERROR);
            return;
        }
        try {
            String nombre = txtNombreEvento.getText().trim();
            String fecha  = txtFechaEvento.getText().trim();
            double precio = Double.parseDouble(txtPrecioBase.getText().trim());

            if (auditorio.eventoExiste(nombre)) {
                Alerta.mostrar("Aviso", "Ya existe un evento con ese nombre",
                        Alert.AlertType.WARNING);
                return;
            }

            Evento nuevoEvento = new Evento(nombre, fecha, precio);
            auditorio.agregarEvento(nuevoEvento);
            listaEventos.add(nuevoEvento);

            // Notificar a los demás clientes por red
            App.red.enviarMensaje("NUEVO_EVENTO:" + nombre + "," + fecha + "," + precio);

            Alerta.mostrar("Evento creado",
                    "El evento \"" + nombre + "\" fue creado correctamente.",
                    Alert.AlertType.INFORMATION);
            limpiarCampos();

        } catch (NumberFormatException e) {
            Alerta.mostrar("Error", "El precio debe ser un número válido.",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void cargarEvento() {
        Evento seleccionado = tableEvento.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Alerta.mostrar("Aviso", "Seleccione un evento en la tabla.",
                    Alert.AlertType.WARNING);
            return;
        }

        eventoActual = seleccionado;
        auditorio.setEventoActual(seleccionado);
        actualizarEtiquetaEventoActivo();

        // Notificar a TODOS los clientes qué evento está en sala
        App.red.enviarMensaje("CARGAR_EVENTO:" + seleccionado.getNombre());
        App.red.enviarMensaje("ACTUALIZAR_TODO");

        Alerta.mostrar("Éxito",
                "Evento \"" + seleccionado.getNombre() + "\" cargado en sala.\n"
                + "Los clientes conectados ya pueden verlo.",
                Alert.AlertType.INFORMATION);
    }

    @FXML
    private void editarEvento() {
        if (eventoActual == null) {
            Alerta.mostrar("Aviso", "Seleccione un evento para editar.",
                    Alert.AlertType.WARNING);
            return;
        }
        if (camposVacios()) {
            Alerta.mostrar("Aviso", "Complete todos los campos.",
                    Alert.AlertType.WARNING);
            return;
        }
        try {
            eventoActual.setNombre(txtNombreEvento.getText().trim());
            eventoActual.setFecha(txtFechaEvento.getText().trim());
            eventoActual.setPrecioBase(
                    Double.parseDouble(txtPrecioBase.getText().trim()));

            tableEvento.refresh();
            actualizarEtiquetaEventoActivo();
            Alerta.mostrar("Actualizado", "Evento modificado correctamente.",
                    Alert.AlertType.INFORMATION);
            limpiarCampos();

        } catch (NumberFormatException e) {
            Alerta.mostrar("Error", "El precio debe ser numérico.",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarEvento() {
        Evento seleccionado = tableEvento.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Alerta.mostrar("Aviso", "No hay evento seleccionado.",
                    Alert.AlertType.WARNING);
            return;
        }
        auditorio.eliminarEvento(seleccionado);
        listaEventos.remove(seleccionado);
        auditorio.getEventosEnCartelera().remove(seleccionado);

        if (seleccionado.equals(eventoActual)) {
            eventoActual = null;
            auditorio.setEventoActual(null);
        }
        actualizarEtiquetaEventoActivo();
        limpiarCampos();
        Alerta.mostrar("Eliminado", "Evento eliminado correctamente.",
                Alert.AlertType.INFORMATION);
    }

    @FXML
    private void reiniciarSala() {
        Evento seleccionado = tableEvento.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Alerta.mostrar("Aviso", "Seleccione un evento para reiniciar.",
                    Alert.AlertType.WARNING);
            return;
        }
        int[][] matriz = seleccionado.getMatrizAsientos();
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                matriz[i][j] = 0;
            }
        }
        Alerta.mostrar("Sala reiniciada",
                "Todos los asientos de \"" + seleccionado.getNombre()
                + "\" fueron liberados.", Alert.AlertType.INFORMATION);
        App.red.enviarMensaje("ACTUALIZAR_TODO");
    }

    // ==================== REPORTES EN VENTANA GUI ====================

    /**
     * Abre una ventana con la tabla detallada de TODAS las compras
     * del evento activo: ID, Cliente, Tipo, Asientos, Precio, Fecha.
     */
    @FXML
    private void verReporte() {
        Evento evento = auditorio.getEventoActual();
        if (evento == null) {
            Alerta.mostrar("Reporte", "No hay evento activo.",
                    Alert.AlertType.INFORMATION);
            return;
        }

        List<Entrada> entradas = evento.getEntradasVendidas();

        // ── Título ──────────────────────────────────────────────────
        Label lblTitulo = new Label("Reporte de Ventas: " + evento.getNombre());
        lblTitulo.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; "
                + "-fx-text-fill: #D4AF37;");

        Label lblSubtitulo = new Label("Fecha: " + evento.getFecha()
                + "   |   Precio base: ₡" + evento.getPrecioBase());
        lblSubtitulo.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaaaaa;");

        // ── Tabla ────────────────────────────────────────────────────
        TableView<Entrada> tabla = new TableView<>();
        tabla.setStyle("-fx-background-color: #2a2d4a; -fx-table-cell-border-color: #3a3d5a;");

        TableColumn<Entrada, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idEntrada"));
        colId.setPrefWidth(55);

        TableColumn<Entrada, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colCliente.setPrefWidth(130);

        TableColumn<Entrada, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().tipoEntrada()));
        colTipo.setPrefWidth(100);

        TableColumn<Entrada, Integer> colCant = new TableColumn<>("Asientos");
        colCant.setCellValueFactory(new PropertyValueFactory<>("cantidadAsientos"));
        colCant.setPrefWidth(75);

        TableColumn<Entrada, String> colDetalle = new TableColumn<>("Detalle de Asientos");
        colDetalle.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDetalleAsientos().replace("\n", " | ")));
        colDetalle.setPrefWidth(200);

        TableColumn<Entrada, String> colPrecio = new TableColumn<>("Precio (₡)");
        colPrecio.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("%.2f", c.getValue().getPrecioFinalCalculado())));
        colPrecio.setPrefWidth(100);

        TableColumn<Entrada, String> colFecha = new TableColumn<>("Fecha Compra");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaCompra"));
        colFecha.setPrefWidth(130);

        tabla.getColumns().addAll(
                colId, colCliente, colTipo, colCant, colDetalle, colPrecio, colFecha);
        tabla.setItems(FXCollections.observableArrayList(entradas));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        // ── Resumen ──────────────────────────────────────────────────
        double totalRecaudado = evento.calcularRecaudacion();
        int    totalAsientos  = entradas.stream()
                .mapToInt(Entrada::getCantidadAsientos).sum();

        Label lblResumen = new Label(
                "Entradas vendidas: " + entradas.size()
                + "   |   Asientos ocupados: " + totalAsientos
                + "   |   Total recaudado: ₡" + String.format("%.2f", totalRecaudado));
        lblResumen.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; "
                + "-fx-text-fill: #ffffff; -fx-background-color: #1e2040; "
                + "-fx-padding: 10; -fx-background-radius: 6;");

        // ── Layout ───────────────────────────────────────────────────
        VBox root = new VBox(12, lblTitulo, lblSubtitulo, tabla, lblResumen);
        root.setStyle("-fx-background-color: #1a1d3a; -fx-padding: 25;");
        root.setPadding(new Insets(25));

        // ── Ventana ──────────────────────────────────────────────────
        Stage stage = new Stage();
        stage.setTitle("Reporte: " + evento.getNombre());
        stage.initModality(Modality.NONE);
        stage.setScene(new Scene(root, 840, 520));
        stage.show();
    }

    /**
     * Abre una ventana con el resumen GLOBAL de todos los eventos:
     * nombre, fecha, entradas vendidas y recaudación por evento.
     */
    @FXML
    private void verReporteGeneral() {
        List<Evento> eventos = auditorio.getArregloEventos();

        // ── Título ──────────────────────────────────────────────────
        Label lblTitulo = new Label("Reporte Global — Todos los Eventos");
        lblTitulo.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; "
                + "-fx-text-fill: #D4AF37;");

        // ── Tabla ────────────────────────────────────────────────────
        TableView<Evento> tabla = new TableView<>();
        tabla.setStyle("-fx-background-color: #2a2d4a;");

        TableColumn<Evento, String> colEvento = new TableColumn<>("Evento");
        colEvento.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEvento.setPrefWidth(200);

        TableColumn<Evento, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colFecha.setPrefWidth(120);

        TableColumn<Evento, String> colBase = new TableColumn<>("Precio Base (₡)");
        colBase.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("%.2f", c.getValue().getPrecioBase())));
        colBase.setPrefWidth(130);

        TableColumn<Evento, Integer> colVentas = new TableColumn<>("Entradas Vendidas");
        colVentas.setCellValueFactory(c -> new SimpleObjectProperty<>(
                c.getValue().getEntradasVendidas().size()));
        colVentas.setPrefWidth(140);

        TableColumn<Evento, String> colRecaudacion = new TableColumn<>("Recaudación (₡)");
        colRecaudacion.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("%.2f", c.getValue().calcularRecaudacion())));
        colRecaudacion.setPrefWidth(150);

        tabla.getColumns().addAll(
                colEvento, colFecha, colBase, colVentas, colRecaudacion);
        tabla.setItems(FXCollections.observableArrayList(eventos));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        // ── Totales globales ─────────────────────────────────────────
        double totalGlobal = auditorio.getRecaudacionGlobal();
        int totalEntradas = eventos.stream()
                .mapToInt(e -> e.getEntradasVendidas().size()).sum();

        Label lblTotal = new Label(
                "Total de eventos: " + eventos.size()
                + "   |   Total entradas: " + totalEntradas
                + "   |   Recaudación global: ₡" + String.format("%.2f", totalGlobal));
        lblTotal.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; "
                + "-fx-text-fill: #ffffff; -fx-background-color: #1e2040; "
                + "-fx-padding: 10; -fx-background-radius: 6;");

        // ── Layout ───────────────────────────────────────────────────
        VBox root = new VBox(12, lblTitulo, tabla, lblTotal);
        root.setStyle("-fx-background-color: #1a1d3a; -fx-padding: 25;");

        // ── Ventana ──────────────────────────────────────────────────
        Stage stage = new Stage();
        stage.setTitle("Reporte Global del Sistema");
        stage.initModality(Modality.NONE);
        stage.setScene(new Scene(root, 780, 480));
        stage.show();
    }

    // ==================== NAVEGACIÓN ====================

    @FXML
    private void regresarLogin() {
        try {
            instanciaActiva = null;
            App.setRoot("AccesoClienteVista");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== UTILIDADES ====================

    private boolean camposVacios() {
        return txtNombreEvento.getText().trim().isEmpty()
                || txtFechaEvento.getText().trim().isEmpty()
                || txtPrecioBase.getText().trim().isEmpty();
    }

    private void limpiarCampos() {
        txtNombreEvento.clear();
        txtFechaEvento.clear();
        txtPrecioBase.clear();
    }
}
