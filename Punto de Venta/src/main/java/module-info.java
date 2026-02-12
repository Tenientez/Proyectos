module com.pumalacticos {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.pumalacticos.controller to javafx.fxml;
    opens com.pumalacticos to javafx.fxml;
    exports com.pumalacticos;
}