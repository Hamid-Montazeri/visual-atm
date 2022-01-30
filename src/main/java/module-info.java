module com.example.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.ibm.icu;
    requires java.sql;

    opens com.example.main to javafx.graphics, javafx.fxml, javafx.base;
    exports com.example.main;
    exports com.example.main.controller;
    opens com.example.main.controller to javafx.fxml;
    opens com.example.main.model to javafx.base;


}