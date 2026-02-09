module com.proyectou {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens com.proyectou to javafx.fxml;
    exports com.proyectou;
}
