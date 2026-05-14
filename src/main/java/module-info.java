module com.erick.pc_1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens com.erick.pc_1 to javafx.fxml;
    exports com.erick.pc_1;

    opens controllers to javafx.fxml;
    exports controllers;

    opens sistema.sistemadesoportetecnicoit.shared.models to javafx.fxml;
    exports sistema.sistemadesoportetecnicoit.shared.models;
    
    opens conexion to javafx.fxml;
    exports conexion;
}
