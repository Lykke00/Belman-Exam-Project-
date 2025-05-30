package dal;

import dk.belman.dal.IDBConnector;
import org.testcontainers.containers.MSSQLServerContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDBConnector implements IDBConnector {
    private final MSSQLServerContainer<?> container;

    public TestDBConnector(MSSQLServerContainer<?> container) {
        this.container = container;
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(
                    container.getJdbcUrl(),
                    container.getUsername(),
                    container.getPassword()
            );
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to test database", e);
        }
    }
}