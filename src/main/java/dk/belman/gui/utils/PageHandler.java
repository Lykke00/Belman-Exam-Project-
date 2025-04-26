package easv.event.gui.utils;

import easv.event.gui.pages.IPageController;
import easv.event.gui.pages.Pages;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import java.util.HashMap;
import java.util.Map;

public class PageHandler {
    private static PageHandler instance;
    private final static Pages DEFAULT_PAGE = Pages.EVENT;

    private Map<Pages, PageData> pageCache = new HashMap<>();

    private Pages currentPage;

    private BorderPane borderPane;

    private PageHandler() {}

    public static PageHandler getInstance() {
        if (instance == null)
            instance = new PageHandler();

        return instance;
    }

    public void setBorderPane(BorderPane borderPane) {
        this.borderPane = borderPane;
        setCurrentPage(DEFAULT_PAGE);
    }

    public void setCurrentPage(Pages page) {
        PageData storePageNode = storeCurrentPage(page);

        if (storePageNode == null || storePageNode.getPageParent() == null)
            return;

        this.borderPane.setCenter(storePageNode.getPageParent());
    }

    public PageData storeCurrentPage(Pages page) {
        if (currentPage != null && page.getPath().equals(currentPage.getPath()))
            return pageCache.get(currentPage);

        FXMLLoader loader;
        Parent pageNode;
        PageData pageData;

        if (!pageCache.containsKey(page)) {
            loader = new FXMLLoader(getClass().getResource(page.getPath()));
            try {
                pageNode = loader.load();
                page.setController(loader.getController());
                Scene scene = new Scene(pageNode);
                pageData = new PageData(pageNode, scene);
                pageCache.put(page, pageData);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load page: " + page.getPath(), e);
            }
        } else {
            pageData = pageCache.get(page);
        }

        if (page.getController() instanceof IPageController) {
            ((IPageController) page.getController()).load();
        }

        currentPage = page;
        return pageData;
    }

    public PageData getStoredPage(Pages page) {
        if (!pageCache.containsKey(page))
            return null;

        return pageCache.get(page);
    }
}
