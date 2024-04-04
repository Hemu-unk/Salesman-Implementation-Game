module com.example.phase1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.desktop;

    opens com.example.phase1 to javafx.fxml;
    exports com.example.phase1;
    exports com.example.phase1.Main;
    opens com.example.phase1.Main to javafx.fxml;
    exports com.example.phase1.Main.Weapon;
    opens com.example.phase1.Main.Weapon to javafx.fxml;
    exports com.example.phase1.Main.Treasure;
    opens com.example.phase1.Main.Treasure to javafx.fxml;
    exports com.example.phase1.Main.Scoreboard;
    opens com.example.phase1.Main.Scoreboard to javafx.fxml;
    exports com.example.phase1.Main.Market;
    opens com.example.phase1.Main.Market to javafx.fxml;
    exports com.example.phase1.Main.Start;
    opens com.example.phase1.Main.Start to javafx.fxml;
}