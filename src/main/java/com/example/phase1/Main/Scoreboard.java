package com.example.phase1.Main;
import javafx.scene.layout.VBox;


public class Scoreboard {

    static VBox createbox(){



        return new VBox(10);

    }

    static String Instruct(int num){
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
