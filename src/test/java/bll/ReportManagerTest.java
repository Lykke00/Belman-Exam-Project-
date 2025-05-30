package bll;

import dal.TestDBConnector;
import dk.belman.be.OperatorReport;
import dk.belman.be.Report;
import dk.belman.be.ReportImage;
import dk.belman.be.User;
import dk.belman.bll.ReportManager;
import dk.belman.bll.UserManager;
import dk.belman.dal.IDBConnector;
import dk.belman.dal.dao.IReportDAO;
import dk.belman.dal.dao.ReportDAO;
import dk.belman.dal.dao.UserDAO;
import dk.belman.gui.common.PictureItemModel;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReportManagerTest {

    @Container
    public static MSSQLServerContainer<?> sqlServer = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
            .acceptLicense()
            .withPassword("StrongPassword123!")
            .withInitScript("database.sql")
            .withConnectTimeoutSeconds(120);

    private IDBConnector dbConnector;
    private IReportDAO reportDAO;
    private ReportManager reportManager;

    @BeforeAll
    public static void setUpClass() {
        if (!sqlServer.isRunning())
            sqlServer.start();


        // Indlæs testdata efter skemaet er oprettet
        try (Connection conn = DriverManager.getConnection(
                sqlServer.getJdbcUrl(),
                sqlServer.getUsername(),
                sqlServer.getPassword())) {

            // Læs testdata SQL-filen
            String testDataSql;
            try (InputStream is = bll.UserManagerTest.class.getClassLoader().getResourceAsStream("test_data.sql")) {
                if (is == null) {
                    throw new IOException("Kunne ikke finde test_data.sql i classpath");
                }
                testDataSql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException("Fejl ved indlæsning af test_data-fil: " + e.getMessage(), e);
            }

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(testDataSql);
            } catch (SQLException e) {
                throw new RuntimeException("Fejl ved udførelse af test_data-SQL: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke oprette forbindelse til test-databasen: " + e.getMessage(), e);
        }
    }

    @BeforeEach
    public void setUp() {
        dbConnector = new TestDBConnector(sqlServer);
        reportDAO = new ReportDAO(dbConnector);
        reportManager = new ReportManager(reportDAO);
    }

    @Test
    public void createReportSuccess_ReturnsBoolean() {
        User user = new User();
        user.setId(1);

        List<ReportImage> photos = new ArrayList<>();
        ReportImage image = new ReportImage(new byte[]{}, "Test comment", "Front");
        photos.add(image);

        OperatorReport report = new OperatorReport("1234AD", user, photos);
        assertDoesNotThrow(() -> {
            boolean result = reportManager.createReport(report);

            Assertions.assertTrue(result, "Oprettelse burde returnere sandt");
        }, "Metode skal ikke kaste en exception");
    }

    @Test
    public void createReportFailNoPictures_ReturnsException() {
        User user = new User();
        user.setId(1);

        OperatorReport report = new OperatorReport("1234AD", user, null);

        Exception exception = assertThrows(Exception.class, () -> {
            reportManager.createReport(report);
        }, "Rapporten skal ikke kunne oprettes, da der ingen billeder er");

        assertNotNull(exception, "Exception skal ikke være null");
    }
}
