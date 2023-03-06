package com.example.deepspaceimageryanalyzer;


import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

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

    Stage stage;

    ColorAdjust colorAdjust = new ColorAdjust();

    public void upload() {
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


    //Deletes and resets the entire application
    public void delete() {
        originalImage.setImage(null);
        alteredImage.setImage(null);
        luminance.setValue(0);
    }

    //Opens up useful help page. Got code from https://stackoverflow.com/questions/10967451/open-a-link-in-browser-with-java-button
    public void about() {
        try {
            String url = "https://www.wit.ie";
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
    }

    //Black and White method based upon user inputted luminance level.
    public void onSetBW() {
        Image image = originalImage.getImage();

        PixelReader pixelReader = image.getPixelReader();

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage grayImage = new WritableImage(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixelReader.getArgb(x, y);

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
    //Initializing array for use
    int[] imageArray;

    public void process() {
        Image imagePicked = alteredImage.getImage();
        //create array the size of the width multiplied by the height
        imageArray = new int[(int) imagePicked.getHeight() * (int) imagePicked.getWidth()];  //height*width of picture
        Color white = new Color(1, 1, 1, 1);
        //go through pixel by pixel, if black { -1 },if white { row*width+column }
        PixelReader pixelReader = alteredImage.getImage().getPixelReader();
        for (int i = 0; i < alteredImage.getImage().getHeight(); i++) {
            for (int j = 0; j < alteredImage.getImage().getWidth(); j++) {
                Color getColor = pixelReader.getColor(j, i);
                //if white set position of array to coords
                if (getColor.equals(white)) {
                    //set white pixel position to the array.
                    imageArray[(i * (int) alteredImage.getImage().getWidth()) + j] = (i * (int) alteredImage.getImage().getWidth()) + j;
                } else
                    imageArray[(i * (int) alteredImage.getImage().getWidth()) + j] = -1; //sets black pixels to -1 in the array. could use -2,3,4....-100 it doesn't matter
            }
        }
        int right;
        int down;
        for (int data = 0; data < imageArray.length; data++) {

            if ((data + 1) < imageArray.length) {
                right = data + 1;
            } else{
                right = imageArray.length-1; //goes to the very end +1 then does -1 once you run out of pixels
            }

            //checks if down is possible
            if ((data + (int) alteredImage.getImage().getWidth()) < imageArray.length) {
                down = data + (int) alteredImage.getImage().getWidth();
            } else{
                down = imageArray.length-1;
            }


            //unions all data that is not -1
            if (imageArray[data] > -1) {
                if(down < imageArray.length && imageArray[down] > -1) { //
                    union(imageArray, data, down);
                }
                if(right < imageArray.length && imageArray[right] > -1 ) {
                    union(imageArray, data, right);
                }

            }

        }



    }



    public void union(int[] imageArray, int a, int b) {
        imageArray[find(imageArray,b)]=find(imageArray,a); //The root of b is made reference a
    }

    // finds the root of each of the pixels
    public int find(int[] imageArray, int data) {
        if(imageArray[data]==data) {
            return data;
        }
        else{
            return find(imageArray, imageArray[data]);
        }
    }

}