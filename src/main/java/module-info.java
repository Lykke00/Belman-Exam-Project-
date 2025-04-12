module dk.belman.belman {
    requires javafx.controls;
    requires javafx.fxml;


    opens dk.belman.belman to javafx.fxml;
    exports dk.belman.belman;
}