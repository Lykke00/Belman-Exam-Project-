package easv.event.gui.utils;

import javafx.scene.Parent;
import javafx.scene.Scene;

// Driller lidt, men prøvet at slette klassen og oprette på ny
public class PageData {
    private Parent pageParent;
    private Scene pageScene;

    public PageData(Parent pageParent, Scene pageScene) {
        this.pageParent = pageParent;
        this.pageScene = pageScene;
    }

    public Parent getPageParent() {
        return pageParent;
    }

    public Scene getPageScene() {
        return pageScene;
    }

    public void setScene(Scene scene) {
        this.pageScene = scene;
    }
}
