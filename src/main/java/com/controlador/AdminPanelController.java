package com.controlador;

import com.app.App;
import com.modelo.Auditorio;
import com.modelo.Evento;
import com.util.Alerta;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminPanelController implements Initializable {

    @FXML
    private TableColumn<?, ?> eventoCol;
    @FXML
    private TableColumn<?, ?> fechaCol;
    @FXML
    private TableColumn<?, ?> precioCol;
    @FXML
    private TableView<Evento> tableEvento;
    @FXML
    private TextField txtNombreEvento;
    @FXML
    private TextField txtFechaEvento;
    @FXML
    private TextField txtPrecioBase;
    @FXML
    private Label lblEventoActivo;

    ObservableList<Evento> listaEventos = FXCollections.observableArrayList();
    private Auditorio auditorio;
    private Evento eventoActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.auditorio = App.auditorio;

        if (auditorio.getArregloEventos()!= null) {
            listaEventos.setAll(auditorio.getArregloEventos());
        }

        eventoCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        fechaCol.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        precioCol.setCellValueFactory(new PropertyValueFactory<>("precioBase"));
        tableEvento.setItems(listaEventos);

        // Listener para selección de tabla
        tableEvento.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                this.eventoActual = newVal;
                txtNombreEvento.setText(newVal.getNombre());
                txtFechaEvento.setText(newVal.getFecha());
                txtPrecioBase.setText(String.valueOf(newVal.getPrecioBase()));
            }
        });

        // Actualizar etiqueta si hay evento cargado en sala
        if (auditorio.getEventoActual() != null) {
            lblEventoActivo.setText("Evento Activo: " + auditorio.getEventoActual().getNombre());
        }
    }

    // CRUD Evento

    @FXML
    private void crearEvento() {
        if (txtNombreEvento.getText().trim().isEmpty() || txtFechaEvento.getText().trim().isEmpty()
                || txtPrecioBase.getText().trim().isEmpty()) {
            Alerta.mostrar("Error", "No pueden quedar espacios en blanco", Alert.AlertType.ERROR);
            return;
        }
        try {
            String nombre = txtNombreEvento.getText();
            String fecha = txtFechaEvento.getText();
            double precio = Double.parseDouble(txtPrecioBase.getText());

            Evento eventoNuevo = new Evento(nombre, fecha, precio);
            auditorio.agregarEvento(eventoNuevo);
            listaEventos.add(eventoNuevo);

            Alerta.mostrar("Evento creado", "El evento se creó correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();

        } catch (NumberFormatException e) {
            Alerta.mostrar("Error", "El precio debe de ser un número", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void cargarEvento() {
        Evento seleccionado = tableEvento.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            Alerta.mostrar("Aviso", "Debe seleccionar un evento en la tabla", Alert.AlertType.WARNING);
            return;
        }

        this.eventoActual = seleccionado;
        lblEventoActivo.setText("Evento Activo: " + seleccionado.getNombre());
        auditorio.setEventoActual(seleccionado);

        Alerta.mostrar("Éxito", "Evento '" + seleccionado.getNombre() + "' cargado en sala.",
                Alert.AlertType.INFORMATION);
    }

    @FXML
    private void editarEvento() {
        // La selección ya actualiza 'eventoActual' gracias al listener
        if (eventoActual == null) {
            Alerta.mostrar("Aviso", "Seleccione un evento de la tabla para editar.", Alert.AlertType.WARNING);
            return;
        }

        if (txtNombreEvento.getText().trim().isEmpty() || txtFechaEvento.getText().trim().isEmpty()
                || txtPrecioBase.getText().trim().isEmpty()) {
            Alerta.mostrar("Aviso", "Complete todos los campos para editar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            eventoActual.setNombre(txtNombreEvento.getText().trim());
            eventoActual.setFecha(txtFechaEvento.getText().trim());
            eventoActual.setPrecioBase(Double.parseDouble(txtPrecioBase.getText().trim()));

            tableEvento.refresh();
            Alerta.mostrar("Evento actualizado", "Los datos del evento fueron modificados.",
                    Alert.AlertType.INFORMATION);
            limpiarCampos();

        } catch (NumberFormatException e) {
            Alerta.mostrar("Error", "El precio debe ser un número.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarEvento() {
        Evento seleccionado = tableEvento.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Alerta.mostrar("Aviso", "No hay evento para eliminar.", Alert.AlertType.WARNING);
            return;
        }
        auditorio.eliminarEvento(seleccionado);
        listaEventos.remove(seleccionado);

        if (seleccionado.equals(eventoActual)) {
            eventoActual = null;
            lblEventoActivo.setText("Evento Activo: Ninguno");
            auditorio.setEventoActual(null);
        }

        limpiarCampos();
        Alerta.mostrar("Evento eliminado", "El evento fue eliminado correctamente.", Alert.AlertType.INFORMATION);
    }

    // Funciones Administrativas

    @FXML
    private void verReporte() {
        Evento evento = auditorio.getEvento();

        if (evento == null) {
            Alerta.mostrar("Reporte", "No hay evento activo.", Alert.AlertType.INFORMATION);
            return;
        }

        double total = evento.calcularRecaudacion();
        Alerta.mostrar("Reporte de Ventas",
                "Total recaudado para el evento \"" + evento.getNombre() + "\": ₡" + String.format("%.2f", total),
                Alert.AlertType.INFORMATION);
    }

    @FXML
    private void verReporteGeneral() {
        double total = auditorio.getRecaudacionGlobal();
        Alerta.mostrar("Reporte Global",
                "Recaudación total del sistema: ₡" + String.format("%.2f", total),
                Alert.AlertType.INFORMATION);
    }

    @FXML
    private void reiniciarSala() {
        Evento seleccionado = tableEvento.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Alerta.mostrar("Aviso", "Seleccione un evento para reiniciar su sala.", Alert.AlertType.WARNING);
            return;
        }

        int[][] matriz = seleccionado.getMatrizAsientos();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                matriz[i][j] = 0;
            }
        }

        if (auditorio.getEventoActual() != null && auditorio.getEventoActual().equals(seleccionado)) {
            VentasDeAsientosController.refrescarBotones();
        }

        Alerta.mostrar("Sala reiniciada",
                "Todos los asientos del evento '" + seleccionado.getNombre() + "' fueron liberados.",
                Alert.AlertType.INFORMATION);
    }

    @FXML
    private void regresarLogin() {
        try {
            App.setRoot("LoginPanel");
        } catch (Exception e) {
            System.out.println("Error al regresar al login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        txtNombreEvento.clear();
        txtFechaEvento.clear();
        txtPrecioBase.clear();
    }
}
