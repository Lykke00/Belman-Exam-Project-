package dk.belman.gui.pages.operator;

import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.gui.utils.PDFPreviewer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class QcPreviewController extends View implements Initializable {

    @FXML
    private VBox vBoxMain;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File file = new File("/Users/lykkebernberg/Desktop/Belman/src/main/resources/jdbc_tutorial.pdf");
        PDFPreviewer pdfPreviewer = new PDFPreviewer(file);

        vBoxMain.getChildren().add(0, pdfPreviewer);
    }
}
