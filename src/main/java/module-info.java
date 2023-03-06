module com.example.deepspaceimageryanalyzer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.deepspaceimageryanalyzer to javafx.fxml;
    exports com.example.deepspaceimageryanalyzer;
}