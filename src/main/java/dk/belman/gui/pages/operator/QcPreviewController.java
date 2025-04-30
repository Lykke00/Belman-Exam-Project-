package dk.belman.gui.pages.operator;

import com.dansoftware.pdfdisplayer.PDFDisplayer;
import com.gluonhq.charm.glisten.mvc.View;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class QcPreviewController extends View implements Initializable {

    @FXML
    private VBox vBoxMain;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PDFDisplayer displayer = new PDFDisplayer();
        try {
            displayer.loadPDF(new URL("https://www.tutorialspoint.com/jdbc/jdbc_tutorial.pdf"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        vBoxMain.getChildren().add(0, displayer.toNode());
    }
}
