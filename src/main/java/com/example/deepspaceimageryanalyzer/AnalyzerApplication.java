package com.example.deepspaceimageryanalyzer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class AnalyzerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AnalyzerApplication.class.getResource("landingScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Deep Space Image Analyzer - 20098723");
        stage.setScene(scene);
        stage.getIcons().add(new Image("https://icons.iconarchive.com/icons/iconsmind/outline/512/Telescope-icon.png"));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}