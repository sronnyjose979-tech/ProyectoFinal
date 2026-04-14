module com.proyectou {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    // Abrir paquetes para FXML
    opens com.controlador to javafx.fxml;
    opens com.app to javafx.fxml;

    // Abrir modelos para TableView (PropertyValueFactory usa reflexión)
    opens com.modelo to javafx.base;

    // Exportar paquetes necesarios
    exports com.app;
    exports com.modelo;
    exports com.controlador;
    exports com.util;
    exports com.red;
}
