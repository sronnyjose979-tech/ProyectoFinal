package com.proyectou;

import static UtilsAlertas.mostrarAlerta.mostrarAlerta;
import com.modelo.Evento;
import com.modelo.Auditorio;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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
    ObservableList<Evento> listaEventos = FXCollections.observableArrayList();

    private Auditorio auditorio;
    private Evento eventoActual;
    VentasDeAsientosController butacas;

    public void setAuditorio(Auditorio auditorio) {
        this.auditorio = auditorio;
    }

    ///
    ///GESTOR DE LOS EVENTO
    ///
    @FXML
    private void crearEvento() {

        try {
            String nombre = txtNombreEvento.getText();
            String fecha = txtFechaEvento.getText();
            double precio = Double.parseDouble(txtPrecioBase.getText());

            Evento eventoNuevo = new Evento(nombre, fecha, precio);
            auditorio.agregarEvento(eventoNuevo);//aca se crea el auditorio
            listaEventos.add(eventoNuevo);

            mostrarAlerta("Evento creado", "El evento se creo correctaamente.", Alert.AlertType.INFORMATION);
            auditorio.cargarEvento(eventoNuevo);
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Erorr", "El precio debe de ser un numero", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error",
                    "Complete todos los campos.",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void cargarEvento() {
        Evento seleccionado = tableEvento.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            return;
        }
        auditorio.cargarEventoEnSala(seleccionado);
//        if (ventaController != null) {
//            ventaController.actualizarVista();
//        }
//        mostrarAlerta("Éxito", "Evento '" + seleccionado.getNombre() + "' cargado en sala.");
    }

    @FXML
    private void editarEvento() {
        if (eventoActual == null) {
            mostrarAlerta("Aviso",
                    "No hay evento para editar.",
                    Alert.AlertType.WARNING);
            return;
        }

        try {
            eventoActual.setNombre(txtNombreEvento.getText());
            eventoActual.setFecha(txtFechaEvento.getText());
            eventoActual.setPrecioBase(
                    Double.parseDouble(txtPrecioBase.getText())
            );

            mostrarAlerta("Evento actualizado",
                    "Los datos del evento fueron modificados.",
                    Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            mostrarAlerta("Error",
                    "El precio debe ser un número.",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarEvento() {
        Evento seleccionado = tableEvento.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Aviso",
                    "No hay evento para eliminar.",
                    Alert.AlertType.WARNING);
            return;
        }
        auditorio.eliminarEvento(seleccionado);
        listaEventos.remove(seleccionado);

        limpiarCampos();

        mostrarAlerta("Evento eliminado",
                "El evento fue eliminado correctamente.",
                Alert.AlertType.INFORMATION);
    }

    ///
    /// FIN GESTOR DE LOS EVENTO
    ///
    //==========================
    /// 
    /// Funciones administrativas
    ///
    @FXML
    private void verReporte() {
        if (eventoActual == null) {
            mostrarAlerta("Reporte",
                    "No hay evento activo.",
                    Alert.AlertType.INFORMATION);
            return;
        }

        double total = eventoActual.calcularRecaudacion();

        mostrarAlerta("Reporte de Ventas",
                "Total recaudado para el evento \"" + eventoActual.getNombre()
                + "\": ₡" + total,
                Alert.AlertType.INFORMATION);
    }

    @FXML
    private void reiniciarSala() {
        auditorio.reiniciarAsientos();

        mostrarAlerta("Sala reiniciada",
                "Todos los asientos fueron liberados.",
                Alert.AlertType.INFORMATION);
    }

    @FXML
    private void regresarLogin() {
        try {
            App.setRoot("loginPanel");
        } catch (Exception e) {
            System.out.println("Error al regresar al login: " + e.getMessage());
        }
    }

    // =========================
    // Métodos auxiliares
    // =========================
    private void limpiarCampos() {
        txtNombreEvento.clear();
        txtFechaEvento.clear();
        txtPrecioBase.clear();
    }

    public void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cargarEstadosDesdeArchivo() {
        // FALTA LOGICA
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.auditorio = App.auditorio;

        if (auditorio.getEventoArrayList() != null) {
            listaEventos.setAll(auditorio.getEventoArrayList());
        }

        eventoCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        fechaCol.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        precioCol.setCellValueFactory(new PropertyValueFactory<>("precioBase"));
        tableEvento.setItems(listaEventos);
    }
}
