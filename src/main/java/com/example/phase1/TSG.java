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

public class TSG extends Application {
    private static final int GRID_SIZE = 9; //grid size
    private static final int CELL_SIZE = 90; // cell size

    // Player 1 attributes
    private ImageView player1View;
    private int player1X = 0; // Player 1 X-coordinate
    private int player1Y = 0; // Player 1 Y-coordinate

    // Player 2 attributes
    private ImageView player2View;
    private int player2X = 0; // Player 2 X-coordinate
    private int player2Y = 0; // Player 2 Y-coordinate

    private boolean player1Turn = true; // Flag to determine current player's turn

    // 2D array to represent the grid cells
    private final Rectangle[][] mapGridCells = new Rectangle[GRID_SIZE][GRID_SIZE];

    @Override
    public void start(Stage stage) {
        // Initialize a grid pane to hold the game map
        GridPane mapGrid = new GridPane();
        mapGrid.setHgap(1); //grid thickness x
        mapGrid.setVgap(1); //grid thickness y

        // Set white background for the map grid
        mapGrid.setStyle("-fx-background-color: white;");

        // Create map grid cells
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setFill(Color.LIGHTSLATEGRAY);
                cell.setStroke(Color.BLACK);
                mapGrid.add(cell, i, j);
                mapGridCells[i][j] = cell;
            }
        }

        // Set castle location in the middle of the grid
        setMapElement(GRID_SIZE / 2, GRID_SIZE / 2, Color.YELLOW);

        // Place different elements randomly on the map
        placeRandomElements(mapGridCells, 8, Color.GREEN); // Treasures
        placeRandomElements(mapGridCells, 20, Color.BLACK); // Walls
        placeRandomElements(mapGridCells, 4, Color.ORANGE); // Markets
        placeRandomElements(mapGridCells, 6, Color.RED); // Traps
        placeRandomElements(mapGridCells, 4, Color.BLUE); // Lost items

        // Initialize and place player 1 on the map
        player1View = createPlayerView("player_pawn.png");
        movePlayerTo(player1View, player1X, player1Y);
        mapGrid.getChildren().add(player1View);

        // Initialize and place player 2 on the map
        player2View = createPlayerView("player_pawn2.png");
        movePlayerTo(player2View, player2X, player2Y);
        mapGrid.getChildren().add(player2View);

        // Adjustments for the game scene
        int sceneWidth = GRID_SIZE * CELL_SIZE + GRID_SIZE - 1; // Adjust for grid lines
        int sceneHeight = GRID_SIZE * CELL_SIZE + GRID_SIZE - 1; // Adjust for grid lines
        Scene scene = new Scene(mapGrid, sceneWidth, sceneHeight);

        // Event handler for key presses to move players
        scene.setOnKeyPressed(e -> {
            if (player1Turn) {
                movePlayer(player1View, e.getCode());
            } else {
                movePlayer(player2View, e.getCode());
            }
        });

        // Set up the stage
        stage.setTitle("Traveling Salesman Game"); // Set title
        stage.setResizable(false); // Make stage non-resizable
        stage.setScene(scene); // Set the scene
        stage.show(); // Show the stage
    }

    // Method to create an ImageView for a player
    private ImageView createPlayerView(String imagePath) {
        ImageView playerView = new ImageView(new Image(imagePath));
        playerView.setFitWidth(CELL_SIZE);
        playerView.setFitHeight(CELL_SIZE);
        return playerView;
    }

    // Method to move a player based on keyboard input
    private void movePlayer(ImageView playerView, KeyCode keyCode) {
        int newX = player1X;
        int newY = player1Y;

        // Determine current player's position
        if (playerView == player2View) {
            newX = player2X;
            newY = player2Y;
        }

        // Update position based on keyboard input
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

        // Check if the move is valid and update the player's position
        if (isValidMove(newX, newY)) {
            movePlayerTo(playerView, newX, newY);
        }
    }

    // Method to check if a move is valid
    private boolean isValidMove(int x, int y) {
        // Check if the new position is within the bounds of the grid
        if (x < 0 || x >= GRID_SIZE || y < 0 || y >= GRID_SIZE) {
            return false;
        }

        // Check if the new position contains a wall
        Color cellColor = (Color) mapGridCells[x][y].getFill();
        return !cellColor.equals(Color.BLACK); // Return false if the cell contains a wall
    }

    // Method to move a player to a new position on the map
    private void movePlayerTo(ImageView playerView, int x, int y) {
        // Update player's position
        if (playerView == player1View) {
            player1X = x;
            player1Y = y;
        } else {
            player2X = x;
            player2Y = y;
        }

        // Set player's position in the grid pane
        GridPane.setColumnIndex(playerView, x);
        GridPane.setRowIndex(playerView, y);

        // Switch turns after each move
        player1Turn = !player1Turn;
    }

    // Method to set an element (e.g., castle, treasure) on the map
    private void setMapElement(int x, int y, Color color) {
        mapGridCells[x][y].setFill(color);
    }

    // Method to randomly place elements (e.g., treasures, walls) on the map
    private void placeRandomElements(Rectangle[][] mapGridCells, int count, Color color) {
        // Generate random indices excluding the castle location
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
            int x = i % GRID_SIZE;
            int y = i / GRID_SIZE;
            if (mapGridCells[x][y].getFill() != Color.YELLOW) {
                indices.add(i);
            }
        }
        Collections.shuffle(indices);

        // Place elements at random positions
        for (int i = 0; i < count; i++) {
            int index = indices.get(i);
            int x = index % GRID_SIZE;
            int y = index / GRID_SIZE;
            setMapElement(x, y, color);
        }
    }

    // Main method to launch the application
    public static void main(String[] args) {
        launch();
    }
}
