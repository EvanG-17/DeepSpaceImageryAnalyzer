package com.example.deepspaceimageryanalyzer;


import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

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
    public HashMap<Integer, ArrayList<Integer>> hashMapStar = new HashMap();
    public int[] starArray;
    public Label starAmountLabel;
    public Button starCircleButton;
    public Button numberStarButton;
    public Button starSizeButton;
    public MenuItem resetCircleButton;
    public MenuItem resetCircleNumButton;

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
//    public void onSetBW() {
//        Image image = originalImage.getImage();
//        PixelReader pixelReader = image.getPixelReader();
//        int width = (int) image.getWidth();
//        int height = (int) image.getHeight();
//        this.starArray = new int[height * width];
//
//        WritableImage grayImage = new WritableImage(width, height);
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                int pixel = pixelReader.getArgb(x, y);
//
//                int red = ((pixel >> 16) & 0xff);
//                int green = ((pixel >> 8) & 0xff);
//                int blue = (pixel & 0xff);
//
//                if (red >= luminance.getValue() && green >= luminance.getValue() && blue >= luminance.getValue()) {
//                    grayImage.getPixelWriter().setColor(x, y, Color.WHITE);
//                    this.starArray[y * width + x] = y * width + x;
//                } else {
//                    grayImage.getPixelWriter().setColor(x, y, Color.BLACK);
//                    this.starArray[y * width + x] = -1;
//                }
//
//            }
//            alteredImage.setImage(grayImage);
//        }
//       this.storeImageInHashMap();
//        this.printArray(width,height);
//        this.mergeElements(width,height);
//    }

    public void onSetBW() {
        if (this.originalImage != null && this.alteredImage != null) {
            Image selectedImage = this.originalImage.getImage();
            PixelReader pixelReaderbw = selectedImage.getPixelReader();
            int height = (int)selectedImage.getHeight();
            int width = (int)selectedImage.getWidth();
            this.starArray = new int[height * width];
            WritableImage blackWhiteImage = new WritableImage(width, height);

            for(int y = 0; y < height; ++y) {
                for(int x = 0; x < width; ++x) {
                    int red = (int)(pixelReaderbw.getColor(x, y).getRed() * 255.0);
                    int green = (int)(pixelReaderbw.getColor(x, y).getGreen() * 255.0);
                    int blue = (int)(pixelReaderbw.getColor(x, y).getBlue() * 255.0);
                    if ((double)red >= this.luminance.getValue() && (double)green >= this.luminance.getValue() && (double)blue >= this.luminance.getValue()) {
                        blackWhiteImage.getPixelWriter().setColor(x, y, Color.WHITE);
                        this.starArray[y * width + x] = y * width + x;
                    } else {
                        blackWhiteImage.getPixelWriter().setColor(x, y, Color.BLACK);
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



    public void storeImageInHashMap() {
        if (this.hashMapStar != null) {
            this.hashMapStar.clear();
        }

        for(int i = 0; i < this.starArray.length; ++i) {
            if (this.starArray[i] != -1) {
                int root = UnionFind.find(this.starArray, i);
                if (!this.hashMapStar.containsKey(root)) {
                    this.hashMapStar.put(root, new ArrayList());
                }

                ((ArrayList)this.hashMapStar.get(root)).add(i);
            }
        }

        System.out.println(this.hashMapStar);
    }

    public void printArray(int width, int height) {
        for(int i = 0; i < this.starArray.length; ++i) {
            if (i % width == 0) {
                System.out.println("");
            }

            int var1 = this.starArray[i];
            System.out.print("" + var1 + " ");
        }

    }

    public void mergeElements(int width, int height) {
        for(int i = 0; i < this.starArray.length; ++i) {
            if (this.starArray[i] >= 0) {
                if (i + 1 < this.starArray.length && i + 1 % width != 0 && this.starArray[i + 1] >= 0) {
                    UnionFind.union(this.starArray, i, i + 1);
                }

                if (i + width < this.starArray.length && this.starArray[i + width] >= 0) {
                    UnionFind.union(this.starArray, i, i + width);
                }
            }
        }

    }


    public int[] getDisjointSetSizes() {
        ArrayList<Integer> sizes = new ArrayList<Integer>();
        for (int key : hashMapStar.keySet()) {
            int root = UnionFind.find(starArray, key);
            if (!sizes.contains(root)) {
                sizes.add(root);
            }
        }
        int[] setSizes = new int[sizes.size()];
        for (int i = 0; i < sizes.size(); i++) {
            int root = sizes.get(i);
            int size = 0;
            for (int key : hashMapStar.keySet()) {
                if (UnionFind.find(starArray, key) == root) {
                    size+= hashMapStar.get(key).size();
                }
            }
            setSizes[i] = size;
            System.out.println("Size of star(s) are  " + root + ": " + size + " pixels.");
        }
        return setSizes;
    }

    public void starBlueCircles() {
        Set<Integer> keySet = this.hashMapStar.keySet();
        int blueCirclesCount = 0;

        for (Integer hashRoot : keySet) {
            System.out.println("" + hashRoot + " " + this.hashMapStar.get(hashRoot));
            ArrayList<Integer> rootList = (ArrayList)this.hashMapStar.get(hashRoot);

            int width = (int)this.originalImage.getImage().getWidth();
            int height = (int)this.originalImage.getImage().getHeight();
            int firstX = Integer.MAX_VALUE;
            int firstY = Integer.MAX_VALUE;
            int lastX = 0;
            int lastY = 0;

            for(int i = 0; i < rootList.size(); ++i) {
                if (rootList.get(i) % width < firstX) {
                    firstX = rootList.get(i) % width;
                }
                if (rootList.get(i) / width < firstY) {
                    firstY = rootList.get(i) / width;
                }
                if (rootList.get(i) % width > lastX) {
                    lastX = rootList.get(i) % width;
                }
                if (rootList.get(i) / width > lastY) {
                    lastY = rootList.get(i) / width;
                }
            }

            Bounds bounds = this.originalImage.getLayoutBounds();
            double XScale = bounds.getWidth() / this.originalImage.getImage().getWidth();
            double YScale = bounds.getHeight() / this.originalImage.getImage().getHeight();

            firstX = (int)((double)firstX * XScale);
            firstY = (int)((double)firstY * YScale);

            lastX = (int)((double)lastX * XScale);
            lastY = (int)((double)lastY * YScale);

            int midX = firstX + (lastX - firstX) / 2;
            int midY = firstY + (lastY - firstY) / 2;

            Circle circle = new Circle();
            circle.setCenterX(midX);
            circle.setCenterY(midY);
            circle.setRadius(Math.max(midX - firstX, midY - firstY));
            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(Color.BLUE);
            circle.setStrokeWidth(1.0);
            circle.setTranslateX(this.originalImage.getLayoutX());
            circle.setTranslateY(this.originalImage.getLayoutY());
            ((Pane)this.originalImage.getParent()).getChildren().add(circle);
            blueCirclesCount++;
        }
        starAmountLabel.setText("Number Of Stars: " + blueCirclesCount);
    }

    public void numberStar() {

        ArrayList<Circle> circleList = new ArrayList();
        for (Object o : ((Pane)this.originalImage.getParent()).getChildren())

            if (o instanceof Circle)
                circleList.add((Circle) o);

        Collections.sort(circleList, (a, b) -> (int)(b.getRadius() - a.getRadius()));
        System.out.println(circleList);
        int count = 0;

        for (Circle c: circleList){
            Text numberS = new Text(c.getCenterX(), c.getCenterY(), count +"");
            numberS.setStroke(Color.ORANGERED);
            numberS.setTranslateX(this.originalImage.getLayoutX());
            numberS.setTranslateY(this.originalImage.getLayoutY());
            ((Pane)this.originalImage.getParent()).getChildren().add(numberS);
            count++;
            if (count > 50) {
                break;
            }
        }
    }


    public void resetBlueCircles(ActionEvent actionEvent) {
        Pane originalImagePane = (Pane) originalImage.getParent();
        originalImagePane.getChildren().removeIf(node -> node instanceof Circle);
    }

    public void resetCircleNumbers(ActionEvent actionEvent) {
        Pane originalImagePane = (Pane) originalImage.getParent();
        originalImagePane.getChildren().removeIf(node -> node instanceof Text);
    }
}