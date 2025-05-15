package dk.belman;

import atlantafx.base.theme.PrimerLight;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.visual.Swatch;
import dk.belman.gui.*;
import dk.belman.gui.components.DrawerManager;
import dk.belman.gui.components.Snackbar;
import dk.belman.gui.utils.LabelStyle;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.attach.util.Services;
import com.gluonhq.attach.util.impl.ServiceFactory;

public class Main extends Application {

    public static File ROOT_DIR;
    private final AppManager appManager = AppManager.initialize(this::postInit);

    private void postInit(Scene scene) {
        if (Platform.isDesktop())
            scene.getStylesheets().add(new PrimerLight().getUserAgentStylesheet());

        StackPane rootStack = new StackPane();
        rootStack.getChildren().add(scene.getRoot());

        scene.setRoot(rootStack);
        Snackbar.setStackPane(rootStack);

        // Setup Views
        Routes.setupViews(appManager);

        // Setup Drawer
        DrawerManager.getInstance();

        Swatch.BLUE.assignTo(scene);
        ((Stage) scene.getWindow()).getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/icon.png"))));
    }

    static {
        if (Platform.isDesktop()) {
            registerStorage();

            ROOT_DIR = Services.get(StorageService.class)
                    .flatMap(StorageService::getPrivateStorage)
                    .orElseThrow(() -> new RuntimeException("Error retrieving private storage"));
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void start(Stage stage) {
        appManager.start(stage);
        stage.setTitle("Belsign QR Report System");

        appManager.switchView(AppView.INSPECTOR_VIEW_REPORTS.getRoute());

        appManager.getAppBar().setTitle(new Label("Belsign"));

        Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logo_white.png")));
        Label title = LabelStyle.getAppBarTitle("Belsign");

        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(25);
        logoView.setFitHeight(25);
        logoView.setPreserveRatio(true);

        HBox logoBox = new HBox(10, logoView, title);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        appManager.getAppBar().setTitle(logoBox);
        appManager.getAppBar().setStyle("-fx-background-color: #00539B;");
    }

    private static void registerStorage() {
        StorageService storageService = new StorageService() {
            @Override
            public Optional<File> getPrivateStorage() {
                return Optional.of(new File(System.getProperty("user.home")));
            }

            @Override
            public Optional<File> getPublicStorage(String subdirectory) {
                return getPrivateStorage();
            }

            @Override
            public boolean isExternalStorageWritable() {
                return getPrivateStorage().get().canWrite();
            }

            @Override
            public boolean isExternalStorageReadable() {
                return getPrivateStorage().get().canRead();
            }
        };

        ServiceFactory<StorageService> storageServiceFactory = new ServiceFactory<>() {
            @Override
            public Class<StorageService> getServiceType() {
                return StorageService.class;
            }

            @Override
            public Optional<StorageService> getInstance() {
                return Optional.of(storageService);
            }
        };

        Services.registerServiceFactory(storageServiceFactory);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
