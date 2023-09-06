// Evan Tynan Geary 20098723 Applied Computing Forensics


package com.example.deepspaceimageryanalyzer;


import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import static javafx.scene.paint.Color.BLACK;

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
    //Hash map used for storing our disjoint sets
    public HashMap<Integer, ArrayList<Integer>> hashMapStar = new HashMap();
    public int[] starArray;
    public Label starAmountLabel;
    public Button starCircleButton;
    public Button numberStarButton;
    public Button starSizeButton;
    public MenuItem resetCircleButton;
    public MenuItem resetCircleNumButton;
    public Button randomColourButton;
    private int width;
    private int height;

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


    public void onSetBW() {
        if (this.originalImage != null && this.alteredImage != null) {
            // get the selected image
            Image selectedImage = this.originalImage.getImage();
            PixelReader pixelReaderbw = selectedImage.getPixelReader();
            int height = (int) selectedImage.getHeight();
            int width = (int) selectedImage.getWidth();

            // initalize array
            this.starArray = new int[height * width];

            // creates a writable image for the black and white image
            WritableImage blackWhiteImage = new WritableImage(width, height);

            // we iterate over each pixel in the image
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    // retrieved the the RGB values of each pixel
                    int red = (int) (pixelReaderbw.getColor(x, y).getRed() * 255.0);
                    int green = (int) (pixelReaderbw.getColor(x, y).getGreen() * 255.0);
                    int blue = (int) (pixelReaderbw.getColor(x, y).getBlue() * 255.0);

                    // we check if the value we checked is greater than or equal to the luminance we selected
                    if ((double) red >= this.luminance.getValue() &&
                            (double) green >= this.luminance.getValue() &&
                            (double) blue >= this.luminance.getValue()) {

                        // we set the pixel color to white
                        blackWhiteImage.getPixelWriter().setColor(x, y, Color.WHITE);
                        this.starArray[y * width + x] = y * width + x;
                    } else {
                        // otherwise we set the pixel color to black
                        blackWhiteImage.getPixelWriter().setColor(x, y, BLACK);
                        this.starArray[y * width + x] = -1;
                    }
                }
            }


            this.alteredImage.setImage(blackWhiteImage);


            this.mergeElements(width, height);
            this.printArray(width, height);
            this.storeImageInHashMap();
        }
    }

    public void randomStarColour(ActionEvent actionEvent) {
        // get the selected image
        Image selectedImage = this.originalImage.getImage();
        PixelReader pixelReader = selectedImage.getPixelReader();
        int height = (int) selectedImage.getHeight();
        int width = (int) selectedImage.getWidth();
        // we create a writable image to store the random color image
        WritableImage randomColorImage = new WritableImage(width, height);
        PixelWriter pixelWriter = randomColorImage.getPixelWriter();

        //for loop iterates over each pixel in our image
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                int index = (row * width) + column;
                int root = UnionFind.find(starArray, index);

                // here if the root of the star component is not present in our hashmap, set the pixel color to black
                if (!hashMapStar.containsKey(root)) {
                    pixelWriter.setColor(column, row, BLACK);
                }
            }
        }

        // we then generate random colours for stars and set the each pixel to the
        // generated colours
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                int index = (row * width) + column;
                int root = UnionFind.find(starArray, index);


                // if our hashmap has the root
                // generate a random color for the star
                if (hashMapStar.containsKey(root)) {


                    int red = (int) (Math.random() * 256);
                    int green = (int) (Math.random() * 256);
                    int blue = (int) (Math.random() * 256);
                    Color color = Color.rgb(red, green, blue);

                    //iterate through entire star and update pixel colour.
                    for (int pixelIndex : hashMapStar.get(root)) {
                        int pixelRow = pixelIndex / width;
                        int pixelColumn = pixelIndex % width;
                        pixelWriter.setColor(pixelColumn, pixelRow, color);


                    }
                }
            }
            //update image view
            originalImage.setImage(randomColorImage);
        }
    }

    //got help here https://stackoverflow.com/questions/7602665/store-an-array-in-hashmap
    public void storeImageInHashMap() {
        if (this.hashMapStar != null) {
            this.hashMapStar.clear();
        }

        //iterate through star array
        for (int i = 0; i < this.starArray.length; ++i) {
            //check if element is background or not
            if (this.starArray[i] != -1) {
                // finds the root of the current element using the UnionFind class and "find" method
                int root = UnionFind.find(this.starArray, i);
                //checks if root is already in the hashMap star
                if (!this.hashMapStar.containsKey(root)) {
                    //creates new array list and associates it with our hashmap Star
                    this.hashMapStar.put(root, new ArrayList());
                }
                // we then add the current element to the ArrayList associated with the root in the hashMapStar
                ((ArrayList) this.hashMapStar.get(root)).add(i);
            }
        }

        System.out.println(this.hashMapStar);
    }

    public void printArray(int width, int height) {
        //iterate through our starArray
        for (int i = 0; i < this.starArray.length; ++i) {
            //we check if current index is at the start of a new row
            if (i % width == 0) {
                System.out.println("");
            }

            int var1 = this.starArray[i];
            System.out.print("" + var1 + " ");
        }

    }

    public void mergeElements(int width, int height) {
        //we iterate through starArray
        for (int i = 0; i < this.starArray.length; ++i) {
            //check if element is background or not
            if (this.starArray[i] >= 0) {
                // we check if there is a neighboring element to the right (i + 1)
                if (i + 1 < this.starArray.length && i + 1 % width != 0 && this.starArray[i + 1] >= 0) {
                    // merges the current element with the neighboring element to the right
                    UnionFind.union(this.starArray, i, i + 1);
                }
                // check if there is a neighboring element below (i + width)aa
                if (i + width < this.starArray.length && this.starArray[i + width] >= 0) {
                    // we finally merge the current element with the neighboring element below
                    UnionFind.union(this.starArray, i, i + width);
                }
            }
        }

    }


    public int[] getDisjointSetSizes() {

        //array list to store our root values
        ArrayList<Integer> sizes = new ArrayList<Integer>();

        //iterate through our hash map
        for (int key : hashMapStar.keySet()) {
            //use Union Find "find" method to find our root
            int root = UnionFind.find(starArray, key);
            //see if root is not here already
            if (!sizes.contains(root)) {
                sizes.add(root);
            }
        }

        // array to store our set sizes
        int[] setSizes = new int[sizes.size()];
        //iterate through root array list we made
        for (int i = 0; i < sizes.size(); i++) {
            //get sizes from our sizes Arraylist
            int root = sizes.get(i);
            int size = 0;

            //iterate through hashMapStar
            for (int key : hashMapStar.keySet()) {
                //we check if the root of the key matches current
                if (UnionFind.find(starArray, key) == root) {
                    //increment size by size of current key
                    size += hashMapStar.get(key).size();
                }
            }
            //store sizes in setSizes
            setSizes[i] = size;
            System.out.println("Size of star(s) at  " + root + " are " + size + " pixels big.");
        }
        return setSizes;
    }


    //Blue star method, has dependencies for many other methods
    public void starBlueCircles() {
        //all keys from our star hash map
        Set<Integer> keySet = this.hashMapStar.keySet();
        int blueCirclesCount = 0;

        //iterate through star hashmap
        for (Integer hashRoot : keySet) {
            System.out.println("" + hashRoot + " " + this.hashMapStar.get(hashRoot));
            ArrayList<Integer> rootList = (ArrayList) this.hashMapStar.get(hashRoot);

            //width and height of original image
            int width = (int) this.originalImage.getImage().getWidth();
            int height = (int) this.originalImage.getImage().getHeight();

            //variables we store MIN and MAX coordinates inside
            int firstX = Integer.MAX_VALUE;
            int firstY = Integer.MAX_VALUE;
            int lastX = 0;
            int lastY = 0;


            //iterare though rootlist
            for (int i = 0; i < rootList.size(); ++i) {

                //update MIN and MAX coordinates
                if (rootList.get(i) % width < firstX) {
                    firstX = rootList.get(i) % width;
                }
                //update MIN and MAX coordinates
                if (rootList.get(i) / width < firstY) {
                    firstY = rootList.get(i) / width;
                }
                //update MIN and MAX coordinates
                if (rootList.get(i) % width > lastX) {
                    lastX = rootList.get(i) % width;
                }
                //update MIN and MAX coordinates
                if (rootList.get(i) / width > lastY) {
                    lastY = rootList.get(i) / width;
                }
            }

            Bounds bounds = this.originalImage.getLayoutBounds();
            double XScale = bounds.getWidth() / this.originalImage.getImage().getWidth();
            double YScale = bounds.getHeight() / this.originalImage.getImage().getHeight();


            //we scale X and Y
            firstX = (int) ((double) firstX * XScale);
            firstY = (int) ((double) firstY * YScale);

            lastX = (int) ((double) lastX * XScale);
            lastY = (int) ((double) lastY * YScale);


            //we find the midpoint of the circle
            int midX = firstX + (lastX - firstX) / 2;
            int midY = firstY + (lastY - firstY) / 2;

            //make a new circle
            Circle circle = new Circle();
            //sets midpoint of new circle
            circle.setCenterX(midX);
            circle.setCenterY(midY);
            //sets radius of new circle
            circle.setRadius(Math.max(midX - firstX, midY - firstY));
            //circle features
            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(Color.BLUE);
            circle.setStrokeWidth(1.0);
            circle.setTranslateX(this.originalImage.getLayoutX());
            circle.setTranslateY(this.originalImage.getLayoutY());
            //adds new circle
            ((Pane) this.originalImage.getParent()).getChildren().add(circle);
            //counts up
            blueCirclesCount++;
        }

        starAmountLabel.setText("Number Of Stars: " + blueCirclesCount);
    }

    public void numberStar() {

        //we make new array list to store our stars
        ArrayList<Circle> circleList = new ArrayList();
        //iterate through node
        for (Object o : ((Pane) this.originalImage.getParent()).getChildren())
            //check if child is a circle
            if (o instanceof Circle)
                //if correct, add to circleList array
                circleList.add((Circle) o);

        //descending order sort
        Collections.sort(circleList, (a, b) -> (int) (b.getRadius() - a.getRadius()));

        System.out.println(circleList);

        int count = 1;

        for (Circle c : circleList) {
            //text to number each individual star
            Text numberS = new Text(c.getCenterX(), c.getCenterY(), count + "");
            //colour of text font
            numberS.setStroke(Color.ORANGERED);

            numberS.setTranslateX(this.originalImage.getLayoutX());
            numberS.setTranslateY(this.originalImage.getLayoutY());
            //add text to parent node
            ((Pane) this.originalImage.getParent()).getChildren().add(numberS);
            count++;
            if (count > 50) {
                break;
            }
        }
    }


    //deletes all blue circles
    public void resetBlueCircles(ActionEvent actionEvent) {
        Pane originalImagePane = (Pane) originalImage.getParent();
        originalImagePane.getChildren().removeIf(node -> node instanceof Circle);
    }

    //deletes all circle numbers
    public void resetCircleNumbers(ActionEvent actionEvent) {
        Pane originalImagePane = (Pane) originalImage.getParent();
        originalImagePane.getChildren().removeIf(node -> node instanceof Text);
    }
}