module com.proyectou {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.proyectou to javafx.fxml;
    exports com.proyectou;
}
