package com.example.phase1.Main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.*;

public class TSG extends Application {
    private static final int GRID_SIZE =9;
    private static final int CELL_SIZE =90;
    private ImageView player1View;
    private int player1X = 0; //player pos x
    private int player1Y = 0; //player pos y
    private ImageView player2View;
    private int player2X = 8; //player2 pos x
    private int player2Y = 8; //player2 pos y
    private boolean player1Turn = true;
    private final Rectangle[][] mapGridCells = new Rectangle[GRID_SIZE][GRID_SIZE];
    private final Map<String, Treasure> player1Treasures = new HashMap<>();
    private final Map<String, Treasure> player2Treasures = new HashMap<>();
    private final Random random = new Random();
    private int remainingSteps = 0;
    private VBox scoreboard;
    private Label player1MoneyLabel;
    private Label player1WeaponLabel;
    private Label player2MoneyLabel;
    private Label player2WeaponLabel;
    private Label player1StrengthLabel;
    private Label player2StrengthLabel;
    private Label player1TreasureLabel;
    private Label player2TreasureLabel;
    private ImageView currentPlayerImageView;
    private static final Treasure[] treasures = {
            new Treasure("Diamond Ring", 100),
            new Treasure("Jewel-encrusted Sword", 150),
            new Treasure("Golden Goblet", 200),
            new Treasure("Crystal Goblets", 120),
            new Treasure("Wooden Bow", 80),
            new Treasure("Paladin’s Shield", 180),
            new Treasure("Golden Key", 90),
            new Treasure("Dragon’s Scroll", 250)
    };
    private final Map<String, Weapon> weaponsMarket = new HashMap<>();
    private final Map<String, Weapon> player1Weapons = new HashMap<>();
    private final Map<String, Weapon> player2Weapons = new HashMap<>();
    private int player1TreasureValue = 0;
    private int player2TreasureValue = 0;
    private boolean player1EnteredCell = false;
    private boolean player2EnteredCell = false;
    private Button rollButton;
    private int player1TreasuresFound = 0;
    private int player2TreasuresFound = 0;
    private boolean gameEnded = false;
    @Override
    public void start(Stage stage) {
        GridPane mapGrid = new GridPane();
        mapGrid.setHgap(1);
        mapGrid.setVgap(1);
        mapGrid.setStyle("-fx-background-color: white;");

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setFill(Color.LIGHTSLATEGRAY);
                cell.setStroke(Color.BLACK);
                mapGrid.add(cell, i, j);
                mapGridCells[i][j] = cell;
                mapGridCells[i][j] = cell;
            }
        }
        setMapElement(GRID_SIZE / 2, GRID_SIZE / 2, Color.YELLOW);
        placeRandomElements(mapGridCells, 10, Color.BLACK);
        placeRandomElements(mapGridCells, 6, Color.ORANGE);
        placeRandomElements(mapGridCells, 7, Color.RED);
        placeRandomElements(mapGridCells, 9, Color.BLUE);
        placeRandomTreasures(mapGridCells, 8);

        rollButton = new Button("Roll Die");
        rollButton.setOnAction(e -> {
            remainingSteps = rollDieAndDisplayResult(); // Store the result of the die roll

            if (player1View == null) {
                player1View = createPlayerView("player_pawn.gif");
                movePlayerTo(player1View, player1X, player1Y);
                mapGrid.getChildren().add(player1View);
                // Set player1Turn to true when player 1 becomes visible
                player1Turn = true;

            } else if (player2View == null) {
                player2View = createPlayerView("player_pawn2.gif");
                movePlayerTo(player2View, player2X, player2Y);
                mapGrid.getChildren().add(player2View);
                // Set player1Turn to false after player 2 becomes visible
                player1Turn = false;
            }
            mapGrid.requestFocus(); // Ensure the map grid has focus for input
            updateScoreboard(); // Update the scoreboard after each roll
        });

        populateWeaponsMarket();

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(rollButton);

        VBox root = new VBox(10);
        root.getChildren().addAll(mapGrid, buttonBox);

        BorderPane rootPane = new BorderPane();
        rootPane.setLeft(root); // Your game layout

        // Initialize and add the scoreboard to the right
        initializeScoreboard();
        rootPane.setRight(scoreboard); // Scoreboard layout

        int sceneWidth = GRID_SIZE * CELL_SIZE + GRID_SIZE - 1 + 260;
        int sceneHeight = GRID_SIZE * CELL_SIZE + GRID_SIZE - 1 + 50; // Extra screen space for button
        Scene scene = new Scene(rootPane, sceneWidth, sceneHeight);

        Map<Color, Image> colorImageMap = new HashMap<>();
        colorImageMap.put(Color.YELLOW, new Image("Castle.png"));
        colorImageMap.put(Color.GREEN, new Image("Treasure.gif"));
        colorImageMap.put(Color.BLACK, new Image("Wall.gif"));
        colorImageMap.put(Color.BLUE, new Image("Lost Items.gif"));
        colorImageMap.put(Color.RED, new Image("Trap.gif"));
        colorImageMap.put(Color.ORANGE, new Image("Market.gif"));

        placeImageInCell(mapGrid, mapGridCells, colorImageMap);

        scene.setOnKeyPressed(e -> {
            if (remainingSteps > 0) {
                if (player1Turn) {
                    movePlayer(player1View, e.getCode());
                } else if (player2View != null) {
                    movePlayer(player2View, e.getCode());
                }
                checkForPlayerCollision(); // checkForPlayerCollision() after each move
                updateScoreboard(); // Update the scoreboard after each move
            }
        });

        stage.setTitle("Traveling Salesman Game");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
    private void placeImageInCell(GridPane mapGrid, Rectangle[][] mapGridCells, Map<Color, Image> colorImageMap) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Color cellColor = (Color) mapGridCells[i][j].getFill();
                if (colorImageMap.containsKey(cellColor)) {
                    ImageView imageView = new ImageView(colorImageMap.get(cellColor));
                    imageView.setFitWidth(CELL_SIZE);
                    imageView.setFitHeight(CELL_SIZE);
                    // Adjust the position of the image to align with the corresponding rectangle
                    GridPane.setColumnIndex(imageView, i);
                    GridPane.setRowIndex(imageView, j);
                    // Add the image to the layout containing the rectangle
                    mapGrid.getChildren().add(imageView);
                }
            }
        }
    }
    private void initializeScoreboard() {
        // Create labels to display player information
        Label player1Label = new Label("Player 1");
        player1Label.setStyle("-fx-font-weight: bold; -fx-underline: true;"); // Bold and underline style
        player1Label.setFont(Font.font("CAMBRIA", FontWeight.BOLD, 15)); // Set font for Player 1 label
        player1MoneyLabel = new Label("Money: " + player1TreasureValue);
        player1WeaponLabel = new Label("Weapon: " + (player1Weapons.isEmpty() ? "None" : player1Weapons.keySet().toString()));
        player1StrengthLabel = new Label("Strength: " + calculatePlayerStrength(player1Weapons));
        player1TreasureLabel = new Label("Treasure Collected: " + player1Treasures.size()); // Display treasure collected

        Label player2Label = new Label("Player 2");
        player2Label.setStyle("-fx-font-weight: bold; -fx-underline: true;"); // Bold and underline style
        player2Label.setFont(Font.font("CAMBRIA", FontWeight.BOLD, 15)); // Set font for Player 2 label
        player2MoneyLabel = new Label("Money: " + player2TreasureValue);
        player2WeaponLabel = new Label("Weapon: " + (player2Weapons.isEmpty() ? "None" : player2Weapons.keySet().toString()));
        player2StrengthLabel = new Label("Strength: " + calculatePlayerStrength(player2Weapons));
        player2TreasureLabel = new Label("Treasure Collected: " + player2Treasures.size()); // Display treasure collected

        // Add label to indicate turn
        Label turnLabel = new Label("Player's Turn");
        turnLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-font-family: 'CAMBRIA'; -fx-underline: true;");

        // Create an ImageView to display the image indicating the current player's turn
        currentPlayerImageView = new ImageView();
        currentPlayerImageView.setFitHeight(20);
        currentPlayerImageView.setFitWidth(20);

        // Create instructions for the game with bullet points
        Label instructions = new Label("Instructions:");
        instructions.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-font-family: 'CAMBRIA'; -fx-underline: true;");
        Label gameInstructions1 = new Label("• Arrow Keys To Move.");
        Label gameInstructions2 = new Label("• Press 'Enter' To Access Markets.");
        Label gameInstructions3 = new Label("• Objective Is To Collect The Most Treasure");
        Label gameInstructions4 = new Label("• Avoid Traps And Strategize To Win!");

        // Add a label for the legend
        Label legendLabel = new Label("Map Component's");
        legendLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-font-family: 'CAMBRIA'; -fx-underline: true;");

        // Create colored boxes to represent map components along with text labels
        VBox legendBox = new VBox(5);

        HBox pair1 = new HBox(10);
        Rectangle box1 = new Rectangle(20, 20, Color.YELLOW);
        Label label1 = new Label("Castle");
        pair1.getChildren().addAll(box1, label1);

        HBox pair2 = new HBox(10);
        Rectangle box2 = new Rectangle(20, 20, Color.GREEN);
        Label label2 = new Label("Treasure");
        pair2.getChildren().addAll(box2, label2);

        HBox pair3 = new HBox(10);
        Rectangle box3 = new Rectangle(20, 20, Color.BLACK);
        Label label3 = new Label("Wall");
        pair3.getChildren().addAll(box3, label3);

        HBox pair4 = new HBox(10);
        Rectangle box4 = new Rectangle(20, 20, Color.ORANGE);
        Label label4 = new Label("Market");
        pair4.getChildren().addAll(box4, label4);

        HBox pair5 = new HBox(10);
        Rectangle box5 = new Rectangle(20, 20, Color.BLUE);
        Label label5 = new Label("Lost Item");
        pair5.getChildren().addAll(box5, label5);

        HBox pair6 = new HBox(10);
        Rectangle box6 = new Rectangle(20, 20, Color.RED);
        Label label6 = new Label("Trap");
        pair6.getChildren().addAll(box6, label6);

        legendBox.getChildren().addAll(pair1, pair2, pair3, pair4, pair5, pair6);
// Create a VBox to hold player information and instructions
        scoreboard = new VBox(10);
        Label scoreboardTitle = new Label("Scoreboard");
        scoreboardTitle.setFont(Font.font("CAMBRIA", FontWeight.BOLD, 20)); // Set the font weight to bold
        scoreboard.getChildren().addAll(
                scoreboardTitle,
                player1Label,
                player1MoneyLabel,
                player1WeaponLabel,
                player1StrengthLabel,
                player1TreasureLabel,
                player2Label,
                player2MoneyLabel,
                player2WeaponLabel,
                player2StrengthLabel,
                player2TreasureLabel,
                turnLabel,
                currentPlayerImageView,
                instructions,
                gameInstructions1,
                gameInstructions2,
                gameInstructions3,
                gameInstructions4,
                legendLabel,
                legendBox
        );
        //scoreboard.setAlignment(Pos.CENTER);
        scoreboard.setPadding(new Insets(10));// Set padding to push the scoreboard away from the edge
    }
    private void updateScoreboard() {
        // Update player 1 information
        player1MoneyLabel.setText("Money: " + player1TreasureValue);
        player1WeaponLabel.setText("Weapon: " + (player1Weapons.isEmpty() ? "None" : player1Weapons.keySet().toString()));
        player1StrengthLabel.setText("Strength: " + calculatePlayerStrength(player1Weapons));
        player1TreasureLabel.setText("Treasure Collected: " + player1Treasures.size());

        // Update player 2 information
        player2MoneyLabel.setText("Money: " + player2TreasureValue);
        player2WeaponLabel.setText("Weapon: " + (player2Weapons.isEmpty() ? "None" : player2Weapons.keySet().toString()));
        player2StrengthLabel.setText("Strength: " + calculatePlayerStrength(player2Weapons));
        player2TreasureLabel.setText("Treasure Collected: " + player2Treasures.size());

        // Update the current player's turn indicator
        if (player1Turn) {
            currentPlayerImageView.setImage(new Image("player_pawn.gif")); // Set image for Player 1's turn
        } else {
            currentPlayerImageView.setImage(new Image("player_pawn2.gif")); // Set image for Player 2's turn
        }
        currentPlayerImageView.setFitWidth(100); // Set the width of the image
        currentPlayerImageView.setFitHeight(100); // Set the height of the image
    }
    private int calculatePlayerStrength(Map<String, Weapon> playerWeapons) {
        int strength = 0;
        for (Weapon weapon : playerWeapons.values()) {
            strength += weapon.getStrength();
        }
        return strength;
    }

    private int rollDieAndDisplayResult() { //Display Die Result
        int rollResult = rollDie();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Die Roll Result");
        alert.setHeaderText(null);
        alert.setContentText("You rolled a " + rollResult + "!");
        alert.showAndWait();
        return rollResult;
    }
    private int rollDie() { //Die Roller
        // Simulate rolling a six-sided die
        return random.nextInt(1) + 1;
    }
    private ImageView createPlayerView(String imagePath) { //Player view on the map
        ImageView playerView = new ImageView(new Image(imagePath));
        playerView.setFitWidth(CELL_SIZE);
        playerView.setFitHeight(CELL_SIZE);
        return playerView;
    }
    private void movePlayer(ImageView playerView, KeyCode keyCode) { // Move input functions.
        int currentX = playerView == player1View ? player1X : player2X;
        int currentY = playerView == player1View ? player1Y : player2Y;

        // Store the initial position to start coloring from
        int startX = currentX;
        int startY = currentY;

        // Calculate the new position based on the key press and remaining steps
        int newX = currentX;
        int newY = currentY;
        switch (keyCode) {
            case UP:
                for (int i = 0; i < remainingSteps; i++) {
                    int nextY = newY - 1;
                    if (isValidMove(newX, nextY)) {
                        newY = nextY;
                    } else {
                        break; // Stop moving if the next position is invalid
                    }
                }
                break;
            case DOWN:
                for (int i = 0; i < remainingSteps; i++) {
                    int nextY = newY + 1;
                    if (isValidMove(newX, nextY)) {
                        newY = nextY;
                    } else {
                        break;
                    }
                }
                break;
            case LEFT:
                for (int i = 0; i < remainingSteps; i++) {
                    int nextX = newX - 1;
                    if (isValidMove(nextX, newY)) {
                        newX = nextX;
                    } else {
                        break;
                    }
                }
                break;
            case RIGHT:
                for (int i = 0; i < remainingSteps; i++) {
                    int nextX = newX + 1;
                    if (isValidMove(nextX, newY)) {
                        newX = nextX;
                    } else {
                        break;
                    }
                }
                break;
            case ENTER:
                if (player1Turn) {
                    purchaseWeapon(player1View, "Player 1");
                } else {
                    purchaseWeapon(player2View, "Player 2");
                }
                break;
            default:
                return;
        }

        // If the player has moved, color the path and update their position
        if (newX != currentX || newY != currentY) {
            colorPath(startX, startY, newX, newY);
            movePlayerTo(playerView, newX, newY);
            remainingSteps = 0;
        }
    }

    private void colorPath(int startX, int startY, int endX, int endY) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(0.5); // Adjusted hue

        // Color the previous cell (starting position) if it's not the same as the current cell
        if (startX != endX || startY != endY) {
            if (!isMapComponent(startX, startY)) {
                mapGridCells[startX][startY].setEffect(colorAdjust);
            }
        }

        // Determine the direction of movement
        int deltaX = endX - startX;
        int deltaY = endY - startY;

        // Color the path between the previous position and the current position
        if (deltaX != 0) {
            // Player moved horizontally
            int signX = Integer.signum(deltaX);
            for (int x = startX + signX; x != endX; x += signX) {
                if (!isMapComponent(x, startY)) { // Check if the cell is a map component
                    mapGridCells[x][startY].setEffect(colorAdjust);
                }
            }
        } else if (deltaY != 0) {
            // Player moved vertically
            int signY = Integer.signum(deltaY);
            for (int y = startY + signY; y != endY; y += signY) {
                if (!isMapComponent(startX, y)) { // Check if the cell is a map component
                    mapGridCells[startX][y].setEffect(colorAdjust);
                }
            }
        }

        // Uncolor the cell the player just moved to
        mapGridCells[endX][endY].setEffect(null);
    }
    private boolean isMapComponent(int x, int y) {
        // Check if the cell is a map component (wall or other elements)
        Color cellColor = (Color) mapGridCells[x][y].getFill();
        return cellColor.equals(Color.YELLOW)
                || cellColor.equals(Color.BLACK)
                || cellColor.equals(Color.ORANGE)
                || cellColor.equals(Color.RED)
                || cellColor.equals(Color.BLUE)
                || cellColor.equals(Color.GREEN);
    }
    private boolean isValidMove(int x, int y) {
        // Check if the move is within the bounds of the map
        if (x < 0 || x >= GRID_SIZE || y < 0 || y >= GRID_SIZE) {
            return true; // Return true to indicate that the move is valid but out of bounds
        }

        Color cellColor = (Color) mapGridCells[x][y].getFill();
        if (cellColor.equals(Color.BLACK) || cellColor.equals(Color.CYAN)) {
            return false; // Prevent entering black or cyan cells
        }
        // Check if any cell along the path has a hued effect
        if (mapGridCells[x][y].getEffect() instanceof ColorAdjust) { //Empty Houses
            return false;
        }
        return true;
    }
    private void sellItems(String playerName) { // Sell items method linked to the "move player to" method
        Map<String, Treasure> playerTreasures = playerName.equals("Player 1") ? player1Treasures : player2Treasures;
        int playerTreasureValue = playerName.equals("Player 1") ? player1TreasureValue : player2TreasureValue;

        // Calculate the total value of all treasures
        int totalValue = playerTreasureValue;
        for (Treasure treasure : playerTreasures.values()) {
            totalValue += treasure.getValue();
        }
        // Display a dialog showing the total value of all items
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Items Sold");
        alert.setHeaderText(null);
        alert.setContentText(playerName + " sold their items for " + totalValue + " gold coins!");
        alert.showAndWait();

        // Set the player's currency to the total value
        if (playerName.equals("Player 1")) {
            player1TreasureValue = totalValue;
        } else {
            player2TreasureValue = totalValue;
        }
        // Reset the player's inventory
        playerTreasures.clear();
    }
    private void movePlayerTo(ImageView playerView, int x, int y) {
        if (playerView == player1View) {
            player1X = x;
            player1Y = y;
            player1EnteredCell = true; // Set player 1's entered cell flag to true
        } else {
            player2X = x;
            player2Y = y;
            player2EnteredCell = true; // Set player 2's entered cell flag to true
        }
        GridPane.setColumnIndex(playerView, x);
        GridPane.setRowIndex(playerView, y);

        // Check if the player has stepped on a trap
        Color cellColor = (Color) mapGridCells[x][y].getFill();
        if (cellColor.equals(Color.RED)) {

            // Deduct 100 money from the corresponding player
            if (player1Turn) {
                player1TreasureValue -= 100;
                player1TreasureValue = Math.max(0, player1TreasureValue);
            } else {
                player2TreasureValue -= 100;
                player2TreasureValue = Math.max(0, player2TreasureValue);
            }
            // Notify the player about the deduction
            String playerName = player1Turn ? "Player 1" : "Player 2";
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Trap Detected");
            alert.setHeaderText(null);
            alert.setContentText(playerName + " stepped on a trap! 100 gold coins have been deducted.");
            alert.showAndWait();
        }
        // Check if the player is on a blue cell
        else if (cellColor.equals(Color.BLUE)) {
            // Generate a random currency value between 100 and 300
            int gainedMoney = random.nextInt(401) + 100; // 100 to 300 inclusive

            // Update the player's money
            if (player1Turn) {
                player1TreasureValue += gainedMoney;
            } else {
                player2TreasureValue += gainedMoney;
            }
            mapGridCells[x][y].setFill(Color.LIGHTSLATEGRAY);

            // Inform the player about the gained money
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Currency Found");
            alert.setHeaderText(null);
            alert.setContentText("You found " + gainedMoney + " gold coins!");
            alert.showAndWait();
        }
        else if (cellColor.equals(Color.GREEN)) {
            Treasure treasure = (Treasure) mapGridCells[x][y].getUserData();
            if (treasure != null) {
                String treasureName = treasure.getName();
                String message = "You found a " + treasureName + "!!";
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Treasure Found");
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
                // Increment the treasure count for the respective player
                if (player1Turn) {
                    player1TreasuresFound++;
                } else {
                    player2TreasuresFound++;
                }
                // Update the scoreboard
                updateScoreboard();
                // Check if a player has found 5 treasures
                if (player1TreasuresFound >= 5 || player2TreasuresFound >= 5) {
                    determineWinner();
                } else if (player1TreasuresFound + player2TreasuresFound == 8) {
                    determineWinner();
                }
                // Add the treasure to the player's inventory
                if (player1Turn) {
                    // Add treasure to player 1's inventory (no immediate money update)
                    player1Treasures.put(treasureName, treasure);
                } else {
                    // Add treasure to player 2's inventory (no immediate money update)
                    player2Treasures.put(treasureName, treasure);
                }
                // Remove the treasure from the map grid cell
                mapGridCells[x][y].setUserData(null);
                mapGridCells[x][y].setFill(Color.LIGHTSLATEGRAY);
            }
        }

        // Check if the player is on the castle (yellow cell)
        else if (cellColor.equals(Color.YELLOW)) {
            if (player1Turn) {
                // Sell items for player 1
                sellItems("Player 1");
            } else {
                // Sell items for player 2
                sellItems("Player 2");
            }
        }
        player1Turn = !player1Turn;
    }
    private void determineWinner() {
        String winner;
        if (player1TreasuresFound > player2TreasuresFound) {
            winner = "Player 1";
        } else if (player2TreasuresFound > player1TreasuresFound) {
            winner = "Player 2";
        } else {
            winner = "It's a tie!";
        }

        // If it's a tie, set winner to an empty string
        if (winner.equals("It's a tie!")) {
            winner = "";
        } else {
            winner = winner + " wins with ";
        }

        // Create a label for winner message with bold styling, specific font, and increased text size
        Label winnerLabel = new Label("Game Over! " + winner + Math.max(player1TreasuresFound, player2TreasuresFound) + " treasures!");
        winnerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-font-family: 'Arial';");

        // Create and show the alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Congratulations! Your The Castle Owner");
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(winnerLabel);
        alert.setOnHidden(event -> Platform.exit()); // Close the application when the alert is closed
        alert.showAndWait();
    }

    private void checkForPlayerCollision() { //player battle mechanism

        if (player1X == player2X && player1Y == player2Y) {
            // Calculate the total strength of weapons for each player
            int player1Strength = player1Weapons.values().stream().mapToInt(Weapon::getStrength).sum();
            int player2Strength = player2Weapons.values().stream().mapToInt(Weapon::getStrength).sum();

            // Determine the winner based on weapon strength
            if (player1Strength > player2Strength) {
                // Player 1 wins
                player2Weapons.values().forEach(weapon -> weapon.setStrength(0)); // Reset player 2's weapons
                int deduction = (int) Math.ceil(player2TreasureValue / 2.0); // Deduct half of player 2's money
                player2TreasureValue -= deduction; // Deduct money from losing player
                movePlayerTo(player2View, 0, 0); // Reset losing player
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Battle Result");
                alert.setHeaderText(null);
                alert.setContentText("Player 2 lost the battle!");
                alert.showAndWait();

            } else if (player2Strength > player1Strength) {
                // Player 2 wins
                player1Weapons.values().forEach(weapon -> weapon.setStrength(0)); // Reset player 1's weapons
                int deduction = (int) Math.ceil(player2TreasureValue / 2.0); // Deduct half of player 2's money
                player1TreasureValue -= deduction; // Deduct money from losing player
                movePlayerTo(player1View, 0, 0); // Reset losing player
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Battle Result");
                alert.setHeaderText(null);
                alert.setContentText("Player 1 lost the battle!");
                alert.showAndWait();

            } else {
                // It's a tie, determine the winner based on who entered the cell first
                if (player1EnteredCell && !player2EnteredCell) {
                    // Player 1 entered the cell first
                    player2Weapons.values().forEach(weapon -> weapon.setStrength(0)); // Reset player 2's weapons
                    movePlayerTo(player2View, 0, 0); // Reset losing player
                } else if (!player1EnteredCell && player2EnteredCell) {
                    // Player 2 entered the cell first
                    player1Weapons.values().forEach(weapon -> weapon.setStrength(0)); // Reset player 1's weapons
                    movePlayerTo(player1View, 0, 0); // Reset losing player
                }
            }
        }
    }
    private void setMapElement(int x, int y, Color color) {
        mapGridCells[x][y].setFill(color);
    }
    private void placeRandomElements(Rectangle[][] mapGridCells, int count, Color color) { //place my map components.
        List<Integer> availableIndices = getAvailableIndices(mapGridCells, color);

        for (int i = 0; i < count && !availableIndices.isEmpty(); i++) {
            Collections.shuffle(availableIndices);
            int index = availableIndices.remove(0);
            int x = index % GRID_SIZE;
            int y = index / GRID_SIZE;
            setMapElement(x, y, color);
        }
    }

    private List<Integer> getAvailableIndices(Rectangle[][] mapGridCells, Color color) {
        List<Integer> availableIndices = new ArrayList<>();
        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
            int x = i % GRID_SIZE;
            int y = i / GRID_SIZE;
            // Exclude the first and last cells
            if ((x == 0 && y == 0) || (x == GRID_SIZE - 1 && y == GRID_SIZE - 1)) {
                continue;
            }
            if (mapGridCells[x][y].getFill() != Color.YELLOW) {
                availableIndices.add(i);
            }
        }
        return availableIndices;
    }
    private void placeRandomTreasures(Rectangle[][] mapGridCells, int count) {
        List<Treasure> treasuresList = new ArrayList<>(Arrays.asList(treasures));
        Collections.shuffle(treasuresList);

        List<Integer> availableIndices = getAvailableIndices(mapGridCells, Color.GREEN);

        // Exclude first cell and last cell
        availableIndices.remove(Integer.valueOf(0)); // Remove first cell
        availableIndices.remove(Integer.valueOf(GRID_SIZE * GRID_SIZE - 1)); // Remove last cell

        Collections.shuffle(availableIndices);

        for (int i = 0; i < count && i < treasuresList.size() && !availableIndices.isEmpty(); i++) {
            int index = availableIndices.get(i);
            int x = index % GRID_SIZE;
            int y = index / GRID_SIZE;

            Treasure treasure = treasuresList.get(i);
            mapGridCells[x][y].setUserData(treasure);
            mapGridCells[x][y].setFill(Color.GREEN);
        }
    }
    private void populateWeaponsMarket() { // Weapon list and price linked to market/purchase weapon
        weaponsMarket.put("Treasure Location", new Weapon("Treasure Location", 0, 200));
        weaponsMarket.put("Sword", new Weapon("Sword", 50, 450));
        weaponsMarket.put("Bow", new Weapon("Bow", 40, 350));
        weaponsMarket.put("Axe", new Weapon("Axe", 30, 250));
    }
    private void purchaseWeapon(ImageView playerView, String playerName) {
        // Check if the player is on an orange cell
        int playerX = playerView == player1View ? player1X : player2X;
        int playerY = playerView == player1View ? player1Y : player2Y;
        Color playerCellColor = (Color) mapGridCells[playerX][playerY].getFill();

        if (!playerCellColor.equals(Color.ORANGE)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot Purchase");
            alert.setHeaderText(null);
            alert.setContentText("You can only purchase weapons in orange markets.");
            alert.showAndWait();
            return;
        }
        Map<String, Weapon> playerWeapons = playerName.equals("Player 1") ? player1Weapons : player2Weapons;
        final int[] playerTreasureValue = {playerName.equals("Player 1") ? player1TreasureValue : player2TreasureValue};

        // Display the player's current money and available items for purchase
        StringBuilder weaponsList = new StringBuilder("Available Items:\n");
        for (String weaponName : weaponsMarket.keySet()) {
            Weapon weapon = weaponsMarket.get(weaponName);
            weaponsList.append(weapon.getName()).append(" - Price: ").append(weapon.getPrice()).append("\n");
        }
        weaponsList.append("\n").append(playerName).append("'s Current Money: ").append(playerTreasureValue[0]);
        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("Purchase Weapon");
        dialog.setHeaderText(weaponsList.toString());

        dialog.setContentText("Enter the name of the item you want to purchase:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(weaponName -> {
            // Process to purchase of other weapons
            Weapon weapon = weaponsMarket.get(weaponName);
            if (weapon != null) {
                if (playerTreasureValue[0] >= weapon.getPrice()) {
                    if (weapon.getName().equals("Treasure Location")) {
                        // Reveal all undiscovered treasures
                        revealUndiscoveredTreasures();
                    } else {
                        playerWeapons.put(weapon.getName(), weapon);
                    }
                    playerTreasureValue[0] -= weapon.getPrice();
                    if (playerName.equals("Player 1")) {
                        player1TreasureValue = playerTreasureValue[0];
                    } else {
                        player2TreasureValue = playerTreasureValue[0];
                    }
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Purchase Successful");
                    alert.setHeaderText(null);
                    alert.setContentText("You have purchased " + weapon.getName());
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Insufficient Money");
                    alert.setHeaderText(null);
                    alert.setContentText("You don't have enough money to purchase " + weapon.getName());
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Weapon");
                alert.setHeaderText(null);
                alert.setContentText("Invalid weapon name. Please enter a valid weapon name.");
                alert.showAndWait();
            }
        });
    }
    private void revealUndiscoveredTreasures() { //turn green cells to pink to reveal the treasure
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Color cellColor = (Color) mapGridCells[i][j].getFill();
                if (cellColor.equals(Color.GREEN)) {
                    Treasure treasure = (Treasure) mapGridCells[i][j].getUserData();
                    if (treasure != null) {
                        mapGridCells[i][j].setFill(Color.PINK);
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        launch();
    }
}