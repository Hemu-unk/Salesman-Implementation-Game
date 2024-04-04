package com.example.phase1.Main.Scoreboard;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Scoreboard {

    public static VBox createbox(){



        return new VBox(10);

    }
////
    public static HBox createbox2(Color color, String name) {
        HBox pair1 = new HBox(10);
        Rectangle box1 = new Rectangle(20, 20, color);
        Label label1 = new Label(name);
        pair1.getChildren().addAll(box1, label1);
        return pair1;
    }
    public static String Instruct(int num){
       String pop = "";
        if (num==1){
            pop = "Instructions:";
            return pop;
        }
        if (num==2){
            pop = "• Arrow Keys To Move.";
            return pop;
        }
        if (num==3){
            pop="• Press 'Enter' To Access Markets.";
            return pop;

        }
        if (num==4){
            pop="• Objective Is To Collect The Most Treasure";
            return pop;
        }
        if (num==5){
            pop="• Avoid Traps And Strategize To Win!";
            return pop;
        }


        return pop;
    }



}
