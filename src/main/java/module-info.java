module com.example.deepspaceimageryanalyzer {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.deepspaceimageryanalyzer to javafx.fxml;
    exports com.example.deepspaceimageryanalyzer;
}