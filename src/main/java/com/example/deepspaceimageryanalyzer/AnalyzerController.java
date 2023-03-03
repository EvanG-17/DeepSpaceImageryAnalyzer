package com.example.deepspaceimageryanalyzer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import java.io.File;

public class AnalyzerController {
    public MenuItem uploadButton;
    public MenuItem deleteButton;
    public MenuItem aboutButton;
    public ImageView originalImage;
    public Button greyscaleButton;
    public ImageView alteredImage;
    public Slider luminance;
    public Slider redSlider;
    public Slider greenSlider;
    public Slider blueSlider;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }


    Stage stage;

    ColorAdjust colorAdjust = new ColorAdjust();






    public void upload(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);

        if (file == null) {
            return;
        }
        Image image = new Image(file.toURI().toString());

        //Setting Original image boundaries
        originalImage.setImage(image);
        originalImage.setFitHeight(512);
        originalImage.setFitWidth(512);
        originalImage.setPreserveRatio(false);

        //Setting Altered image boundaries
        alteredImage.setImage(image);
        alteredImage.setFitHeight(512);
        alteredImage.setFitWidth(512);
        alteredImage.setPreserveRatio(false);

//        File file2 = new File(String.valueOf(image));
//        long size = file2.length();
//        double sizeInMB = size / (1024.0 * 1024.0);
    }

    public void delete(ActionEvent actionEvent) {
        originalImage.setImage(null);
        alteredImage.setImage(null);
        luminance.setValue(0);
    }

    public void about(ActionEvent actionEvent) {


    }

    public void onSetGreyscale(ActionEvent actionEvent) {
        Image image = originalImage.getImage();


        if (image == null) {
            System.out.println("No image selected");
            return;
        }
        PixelReader pixelReader = image.getPixelReader();

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage grayImage = new WritableImage(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixelReader.getArgb(x, y);



                int alpha = ((pixel >> 24) & 0xff);
                int red = ((pixel >> 16) & 0xff);
                int green = ((pixel >> 8) & 0xff);
                int blue = (pixel & 0xff);


                if(red >= luminance.getValue() && green >= luminance.getValue() && blue >= luminance.getValue())
                    grayImage.getPixelWriter().setColor(x, y, Color.WHITE);
                else
                    grayImage.getPixelWriter().setColor(x, y, Color.BLACK);

            }

            alteredImage.setImage(grayImage);

        }
    }



    public void luminanceSlider() {
//        colorAdjust.setBrightness(luminance.getValue());
//        alteredImage.setEffect(colorAdjust);
    }

}