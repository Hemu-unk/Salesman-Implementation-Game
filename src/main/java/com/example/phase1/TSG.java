package com.example.phase1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TSG extends Application {
    private static final int GRID_SIZE = 10;
    private static final int CELL_SIZE = 100; // Adjust as needed

    private ImageView player1View;
    private ImageView player2View;
    private int player1X = 0; //player 1 location
    private int player1Y = 0; //player 1 location
    private int player2X = 0; //player 2 location
    private int player2Y = 0; //player 2 location
    private boolean player1Turn = true;

    private final Rectangle[][] mapGridCells = new Rectangle[GRID_SIZE][GRID_SIZE];

    @Override
    public void start(Stage stage) {
        GridPane mapGrid = new GridPane();
        mapGrid.setHgap(1);
        mapGrid.setVgap(1);

        // Set white background
        mapGrid.setStyle("-fx-background-color: white;");

        // Create map grid cells
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setFill(Color.BLACK); // Default color
                mapGrid.add(cell, i, j);
                mapGridCells[i][j] = cell;
            }
        }

        // Set castle location in the middle
        setMapElement(GRID_SIZE / 2, GRID_SIZE / 2, Color.YELLOW);

        // Place treasures randomly
        placeRandomElements(mapGridCells, 8, Color.GREEN);

        // Place walls randomly
        placeRandomElements(mapGridCells, 12, Color.WHITE); // Adjust the number of walls as needed

        // Place markets randomly
        placeRandomElements(mapGridCells, 4, Color.ORANGE);

        // Place traps randomly
        placeRandomElements(mapGridCells, 6, Color.RED);

        // Place lost items randomly
        placeRandomElements(mapGridCells, 4, Color.BLUE);

        // Initialize players
        player1View = createPlayerView("player_pawn.png");
        movePlayerTo(player1View, player1X, player1Y);
        mapGrid.getChildren().add(player1View);

        player2View = createPlayerView("player_pawn2.png");
        movePlayerTo(player2View, player2X, player2Y);
        mapGrid.getChildren().add(player2View);

        Scene scene = new Scene(mapGrid, GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
        scene.setOnKeyPressed(e -> {
            if (player1Turn) {
                movePlayer(player1View, e.getCode());
            } else {
                movePlayer(player2View, e.getCode());
            }
        });
        stage.setTitle("Traveling Salesman Game");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private ImageView createPlayerView(String imagePath) {
        ImageView playerView = new ImageView(new Image(imagePath));
        playerView.setFitWidth(CELL_SIZE);
        playerView.setFitHeight(CELL_SIZE);
        return playerView;
    }

    private void movePlayer(ImageView playerView, KeyCode keyCode) {
        int newX = player1X;
        int newY = player1Y;

        if (playerView == player2View) {
            newX = player2X;
            newY = player2Y;
        }

        switch (keyCode) {
            case UP:
                newY = Math.max(0, newY - 1);
                break;
            case DOWN:
                newY = Math.min(GRID_SIZE - 1, newY + 1);
                break;
            case LEFT:
                newX = Math.max(0, newX - 1);
                break;
            case RIGHT:
                newX = Math.min(GRID_SIZE - 1, newX + 1);
                break;
            default:
                return; // Do nothing for other keys
        }

        if (isValidMove(newX, newY)) {
            movePlayerTo(playerView, newX, newY);
        }
    }

    private boolean isValidMove(int x, int y) {
        // Check if the new position is within the bounds of the grid
        if (x < 0 || x >= GRID_SIZE || y < 0 || y >= GRID_SIZE) {
            return false;
        }

        // Check if the new position contains a wall
        Color cellColor = (Color) mapGridCells[x][y].getFill();
        return !cellColor.equals(Color.WHITE); // Return false if the cell contains a wall
    }


    private void movePlayerTo(ImageView playerView, int x, int y) {
        if (playerView == player1View) {
            player1X = x;
            player1Y = y;
        } else {
            player2X = x;
            player2Y = y;
        }

        GridPane.setColumnIndex(playerView, x);
        GridPane.setRowIndex(playerView, y);
        player1Turn = !player1Turn; // Switch turns after each move
    }

    private void setMapElement(int x, int y, Color color) {
        mapGridCells[x][y].setFill(color);
    }

    private void placeRandomElements(Rectangle[][] mapGridCells, int count, Color color) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);

        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int index = indices.get(i);
            int x = index % GRID_SIZE;
            int y = index / GRID_SIZE;
            setMapElement(x, y, color);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
