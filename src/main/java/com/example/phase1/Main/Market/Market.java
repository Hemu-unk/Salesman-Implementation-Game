package com.example.phase1.Main.Market;

import javafx.scene.control.Alert;
import javafx.scene.paint.Color;

public class Market {


public static void purchasechecker(Color playerCellColor){
    if (!playerCellColor.equals(javafx.scene.paint.Color.ORANGE)) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Cannot Purchase");
        alert.setHeaderText(null);
        alert.setContentText("You can only purchase weapons in orange markets.");
        alert.showAndWait();
        return;
    }


}



}



