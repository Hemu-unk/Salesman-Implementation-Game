package com.example.phase1.Main;

import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import java.awt.*;

public class Market {


static void purchasechecker(Color playerCellColor){
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
