module com.pumalacticos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;             
    requires org.xerial.sqlitejdbc;

    opens com.pumalacticos.controller to javafx.fxml;
    opens com.pumalacticos to javafx.fxml;
    opens com.pumalacticos.model.domain to javafx.base;

    exports com.pumalacticos;
    exports com.pumalacticos.controller;
}