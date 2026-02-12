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
                "No hay evento activo.",
                Alert.AlertType.INFORMATION);
        return;
    }

    double total = eventoActual.calcularRecaudacion();

    mostrarAlerta("Reporte de Ventas",
            "Total recaudado para el evento \"" + eventoActual.getNombre() +
            "\": ₡" + total,
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



