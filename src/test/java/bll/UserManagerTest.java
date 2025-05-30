package bll;

import dal.TestDBConnector;
import dk.belman.be.User;
import dk.belman.bll.UserManager;
import dk.belman.dal.IDBConnector;
import dk.belman.dal.dao.IUserDAO;
import dk.belman.dal.dao.UserDAO;
import dk.belman.enums.UserRole;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserManagerTest {

    @Container
    public static MSSQLServerContainer<?> sqlServer = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
            .acceptLicense()
            .withPassword("StrongPassword123!")
            .withInitScript("database.sql")
            .withConnectTimeoutSeconds(120);

    private IDBConnector dbConnector;
    private IUserDAO userDAO;
    private UserManager userManager;

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
            try (InputStream is = UserManagerTest.class.getClassLoader().getResourceAsStream("test_data.sql")) {
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
        userDAO = new UserDAO(dbConnector);
        userManager = new UserManager(userDAO);
    }

    @Test
    public void getAllUsersFromDatabase_SuccessfulIfNotNullOrEmpty() {
        assertDoesNotThrow(() -> {
            List<User> users = userManager.getAll();

            assertNotNull(users);
            assertFalse(users.isEmpty());
        }, "Metode skal ikke kaste en exception");
    }

    @Test
    @Order(1)
    public void registerUser_SuccessfulIfRegisteredIsNotNull() {
        assertDoesNotThrow(() -> {
            User user = new User("SB-12345", "Kasper", "Hansen", "adgangskode1", UserRole.OPERATOR);
            User registered = userManager.registerUser(user);

            assertNotNull(registered);
            assertTrue(registered.getId() > 0);
            assertEquals(user.getEmail(), registered.getEmail());
        }, "Metode skal ikke kaste en exception");
    }


    @Test
    @Order(2)
    public void registerUser_ThrowsErrorIfWorkerIDExists() {
        //bliver registreret fra test_data.sql
        User user = new User("OP001", "Kasper", "Hansen", "adgangskode1", UserRole.OPERATOR);

        Exception exception = assertThrows(Exception.class, () -> {
            userManager.registerUser(user);
        }, "Brugeren skal ikke kunne registreres, da workerid eksisterer");

        assertEquals("USER_EXISTS",  exception.getMessage());
    }

    @Test
    @Order(3)
    public void authenticateUserSuccess_SuccessfulIfReturnIsNotNull() {
        String workerId = "SB-12345";
        String password = "adgangskode1";

        assertDoesNotThrow(() -> {
            User loggedIn = userManager.authenticateUser(workerId, password);

            assertNotNull(loggedIn);
            assertEquals(workerId, loggedIn.getWorkerId());
        }, "Brugeren kunne ikke logges på");
    }

    @Test
    @Order(4)
    public void authenticateUserIncorrectPassword_ReturnsErrorMessage() {
        String workerId = "SB-12345";
        String password = "forkertPW";

        Exception exception = assertThrows(Exception.class, () -> {
            userManager.authenticateUser(workerId, password);
        }, "Brugeren skal ikke kunne logge ind, da adgangskoden er forkert");

        assertEquals("PASSWORD_INCORRECT",  exception.getMessage());
    }

    @Test
    @Order(5)
    public void authenticateUserDoesntExist_ReturnsErrorMessage() {
        String workerId = "ABCD";
        String password = "Kode123";

        Exception exception = assertThrows(Exception.class, () -> {
            userManager.authenticateUser(workerId, password);
        });
        assertEquals("DOESNT_EXIST",  exception.getMessage());
    }
}