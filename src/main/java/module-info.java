module com.proyectou {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens com.proyectou to javafx.fxml;
    opens com.modelo to javafx.base;
    exports com.proyectou;
}
