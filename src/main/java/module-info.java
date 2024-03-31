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
}