package dk.belman.dal;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDBConnector {
    Connection getConnection() throws SQLException;
}