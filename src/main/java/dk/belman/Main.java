package dk.belman;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.charm.glisten.visual.Swatch;
import dk.belman.gui.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.attach.util.Services;
import com.gluonhq.attach.util.impl.ServiceFactory;

import javafx.stage.StageStyle;

public class Main extends Application {

    //  public static final File ROOT_DIR;
    private final AppManager appManager = AppManager.initialize(this::postInit);


    private void postInit(Scene scene) {
        // Setup Views
        appManager.addViewFactory(Routes.HOME, ViewLoader::loadMainView);
        appManager.addViewFactory(Routes.OTHER_PAGE, ViewLoader::loadOtherPageView);

        // Setup Drawer
        DrawerManager.getInstance();

        Swatch.BLUE.assignTo(scene);
        ((Stage) scene.getWindow()).getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/icon.png"))));

    }

    static {
        registerStorage();

        /*
        ROOT_DIR = Services.get(StorageService.class)
                .flatMap(StorageService::getPrivateStorage)
                .orElseThrow(() -> new RuntimeException("Error retrieving private storage"));*/
    }

    private Stage primaryStage;

    @Override
    public void init() {

    }

    @Override
    public void start(Stage stage) {
        appManager.start(stage);
        stage.setTitle("Belsign QR Report System");

        AppManager.getInstance().switchView(Routes.OTHER_PAGE);

        appManager.switchView(Routes.HOME);
        appManager.getAppBar().setNavIcon(MaterialDesignIcon.MENU.button(e -> DrawerManager.getInstance().getDrawer().open()));
    }

    private static void registerStorage() {
        /*
        PlatformUtils.registerStorage();

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

        Services.registerServiceFactory(storageServiceFactory);*/
    }

    public static void main(String[] args) {
        launch(args);
    }
}
