/*package com.proyectou;

import com.modelo.Evento;
import com.modelo.Auditorio;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField; 

public class AdminPanelController {
      @FXML
    private TextField txtNombreEvento;

    @FXML
    private TextField txtFechaEvento;

    @FXML
    private TextField txtPrecioBase;
    
    private Auditorio auditorio;
    private Evento eventoActual;
    
    public void  initialize(){
        auditorio = new Auditorio();
        
    }
    
    ///
    ///GESTOR DE LOS EVENTO
    ///
    @FXML
    private void crearEvento(){
        try {
            String nombre = txtNombreEvento.getText();
            String fecha = txtFechaEvento.getText();
            double precio = Double.parseDouble(txtPrecioBase.getText());
            
            eventoActual = new Evento(nombre, fecha, precio);
            
            mostrarAlerta("Evento creado","El evento se creo correctaamente.", Alert.AlertType.INFORMATION);
            
        } catch(NumberFormatException e){
            mostrarAlerta("Erorr", "El precio debe de ser un numero", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error",
                    "Complete todos los campos.",
                    Alert.AlertType.ERROR);
        }
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
        if (eventoActual == null) {
            mostrarAlerta("Aviso",
                    "No hay evento para eliminar.",
                    Alert.AlertType.WARNING);
            return;
        }

        eventoActual = null;
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
                    "No hay ventas registradas.",
                    Alert.AlertType.INFORMATION);
            return;
        }

        // Aquí luego se puede sumar lo vendido
        mostrarAlerta("Reporte de Ventas",
                "Funcionalidad de reporte en construcción.",
                Alert.AlertType.INFORMATION);
    }

    @FXML
    private void reiniciarSala() {
        auditorio.reiniciarAsientos();

        mostrarAlerta("Sala reiniciada",
                "Todos los asientos fueron liberados.",
                Alert.AlertType.INFORMATION);
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
}
*/



package com.proyectou;

import com.modelo.Evento;
import com.modelo.Auditorio;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class AdminPanelController {

    @FXML
    private TextField txtNombreEvento;

    @FXML
    private TextField txtFechaEvento;

    @FXML
    private TextField txtPrecioBase;

    private Evento eventoActual;
    private Auditorio auditorio;

    @FXML
    public void initialize() {
        auditorio = new Auditorio(); // aunque esté vacío, no falla
    }

    // =========================
    // Gestión de eventos
    // =========================

    @FXML
    private void crearEvento() {
        try {
            String nombre = txtNombreEvento.getText();
            String fecha = txtFechaEvento.getText();
            double precio = Double.parseDouble(txtPrecioBase.getText());

            eventoActual = new Evento(nombre, fecha, precio);

            mostrarAlerta("Evento creado",
                    "Evento creado correctamente.",
                    Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            mostrarAlerta("Error",
                    "El precio debe ser numérico.",
                    Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error",
                    "Complete todos los campos.",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void editarEvento() {
        mostrarAlerta("No disponible",
                "La edición de eventos no está implementada en el modelo.",
                Alert.AlertType.WARNING);
    }

    @FXML
    private void eliminarEvento() {
        if (eventoActual == null) {
            mostrarAlerta("Aviso",
                    "No hay evento para eliminar.",
                    Alert.AlertType.WARNING);
            return;
        }

        eventoActual = null;
        limpiarCampos();

        mostrarAlerta("Evento eliminado",
                "Evento eliminado correctamente.",
                Alert.AlertType.INFORMATION);
    }

    // =========================
    // Acciones administrativas
    // =========================

    @FXML
    private void verReporte() {
        mostrarAlerta("Reporte",
                "Reporte de recaudación pendiente de implementación.",
                Alert.AlertType.INFORMATION);
    }

    @FXML
    private void reiniciarSala() {
        mostrarAlerta("Reiniciar sala",
                "Funcionalidad pendiente de implementación en el modelo.",
                Alert.AlertType.INFORMATION);
    }

    // =========================
    // Utilidades
    // =========================

    private void limpiarCampos() {
        txtNombreEvento.clear();
        txtFechaEvento.clear();
        txtPrecioBase.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
