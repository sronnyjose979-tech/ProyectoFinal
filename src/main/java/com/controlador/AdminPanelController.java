package com.controlador;

import com.app.App;
import com.modelo.Auditorio;
import com.modelo.Evento;
import com.util.Alerta;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private TableColumn<Evento, String> eventoCol;

    @FXML
    private TableColumn<Evento, String> fechaCol;

    @FXML
    private TableColumn<Evento, Double> precioCol;

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

    private ObservableList<Evento> listaEventos;
    private Auditorio auditorio;
    private Evento eventoActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        auditorio = App.auditorio;
        listaEventos = FXCollections.observableArrayList();

        if (auditorio.getArregloEventos() != null) {
            listaEventos.setAll(auditorio.getArregloEventos());
        }

        configurarTabla();
        configurarListenerSeleccion();

        actualizarEtiquetaEventoActivo();
    }

    private void configurarTabla() {
        eventoCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        fechaCol.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        precioCol.setCellValueFactory(new PropertyValueFactory<>("precioBase"));
        tableEvento.setItems(listaEventos);
    }

    private void configurarListenerSeleccion() {

        tableEvento.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Evento>() {

            @Override
            public void changed(ObservableValue<? extends Evento> observable,
                    Evento oldValue, Evento newValue) {

                if (newValue != null) {
                    eventoActual = newValue;
                    txtNombreEvento.setText(newValue.getNombre());
                    txtFechaEvento.setText(newValue.getFecha());
                    txtPrecioBase.setText(String.valueOf(newValue.getPrecioBase()));
                }
            }
        });
    }

    private void actualizarEtiquetaEventoActivo() {

        if (auditorio.getEventoActual() != null) {
            lblEventoActivo.setText("Evento Activo: " + auditorio.getEventoActual().getNombre());
        } else {
            lblEventoActivo.setText("Evento Activo: Ninguno");
        }
    }

    @FXML
    private void crearEvento() {

        if (camposVacios()) {
            Alerta.mostrar("Error", "No pueden quedar espacios en blanco", Alert.AlertType.ERROR);
            return;
        }

        try {

            String nombre = txtNombreEvento.getText().trim();
            String fecha = txtFechaEvento.getText().trim();
            double precio = Double.parseDouble(txtPrecioBase.getText().trim());

            Evento nuevoEvento = new Evento(nombre, fecha, precio);

            auditorio.agregarEvento(nuevoEvento);
            listaEventos.add(nuevoEvento);
            
            // ENVIAR POR RED
            App.red.enviarMensaje("NUEVO_EVENTO:" + nombre + "," + fecha + "," + precio);

            Alerta.mostrar("Evento creado", "El evento se creó correctamente.", Alert.AlertType.INFORMATION);

            limpiarCampos();

        } catch (NumberFormatException e) {

            Alerta.mostrar("Error", "El precio debe ser un número.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void cargarEvento() {

        Evento seleccionado = tableEvento.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {

            Alerta.mostrar("Aviso", "Debe seleccionar un evento en la tabla.", Alert.AlertType.WARNING);
            return;
        }

        eventoActual = seleccionado;
        auditorio.setEventoActual(seleccionado);

        actualizarEtiquetaEventoActivo();
        App.red.enviarMensaje("ACTUALIZAR_TODO");

        Alerta.mostrar("Éxito", "Evento cargado en sala correctamente.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void editarEvento() {

        if (eventoActual == null) {

            Alerta.mostrar("Aviso", "Seleccione un evento para editar.", Alert.AlertType.WARNING);
            return;
        }

        if (camposVacios()) {

            Alerta.mostrar("Aviso", "Complete todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        try {

            eventoActual.setNombre(txtNombreEvento.getText().trim());

            eventoActual.setFecha(txtFechaEvento.getText().trim());

            eventoActual.setPrecioBase(Double.parseDouble(txtPrecioBase.getText().trim()));

            tableEvento.refresh();

            Alerta.mostrar("Actualizado", "Evento modificado correctamente.", Alert.AlertType.INFORMATION);

            limpiarCampos();

        } catch (NumberFormatException e) {

            Alerta.mostrar("Error", "El precio debe ser numérico.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarEvento() {

        Evento seleccionado = tableEvento.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {

            Alerta.mostrar("Aviso", "No hay evento seleccionado.", Alert.AlertType.WARNING);
            return;
        }

        auditorio.eliminarEvento(seleccionado);
        listaEventos.remove(seleccionado);

        if (seleccionado.equals(eventoActual)) {
            eventoActual = null;
            auditorio.setEventoActual(null);
        }
        auditorio.getEventosEnCartelera().remove(seleccionado);

        if (seleccionado.equals(eventoActual)) {
            eventoActual = null;
            auditorio.setEventoActual(null);
        }

        actualizarEtiquetaEventoActivo();
        limpiarCampos();

        actualizarEtiquetaEventoActivo();
        limpiarCampos();

        Alerta.mostrar("Eliminado", "Evento eliminado correctamente.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void verReporte() {

        Evento evento = auditorio.getEventoActual();

        if (evento == null) {

            Alerta.mostrar("Reporte", "No hay evento activo.", Alert.AlertType.INFORMATION);
            return;
        }

        double total = evento.calcularRecaudacion();

        Alerta.mostrar("Reporte de Ventas", "Total recaudado para el evento \"" + evento.getNombre() + "\": ₡" + String.format("%.2f", total), Alert.AlertType.INFORMATION);
    }

    @FXML
    private void verReporteGeneral() {

        double total = auditorio.getRecaudacionGlobal();

        Alerta.mostrar("Reporte Global", "Recaudación total del sistema: ₡" + String.format("%.2f", total), Alert.AlertType.INFORMATION);
    }

    @FXML
    private void reiniciarSala() {

        Evento seleccionado = tableEvento.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {

            Alerta.mostrar("Aviso", "Seleccione un evento para reiniciar.", Alert.AlertType.WARNING);
            return;
        }

        int[][] matriz = seleccionado.getMatrizAsientos();

        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                matriz[i][j] = 0;
            }
        }

        Alerta.mostrar("Sala reiniciada", "Todos los asientos fueron liberados.", Alert.AlertType.INFORMATION);
        App.red.enviarMensaje("ACTUALIZAR_TODO");
    }

    @FXML
    private void regresarLogin() {

        try {
            App.setRoot("AccesoClienteVista");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean camposVacios() {

        return txtNombreEvento.getText().trim().isEmpty() || txtFechaEvento.getText().trim().isEmpty() || txtPrecioBase.getText().trim().isEmpty();
    }

    private void limpiarCampos() {

        txtNombreEvento.clear();
        txtFechaEvento.clear();
        txtPrecioBase.clear();
    }
}
