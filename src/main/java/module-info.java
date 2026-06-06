module com.example.pointeuseapp {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.pointeuseapp.controller to javafx.fxml;
    opens com.example.pointeuseapp.model to javafx.fxml;

    exports com.example.pointeuseapp;
    exports com.example.pointeuseapp.controller;
    exports com.example.pointeuseapp.model;
}