package dk.belman.dal;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;

public class DBConnector implements IDBConnector {
    //designate file path for db settings
    private static final String PROPERTIES_FILE = "config/config.settings";
    private final SQLServerDataSource dataSource;

    public DBConnector() throws IOException {
        //load db settings to properties
        Properties props = new Properties();
        props.load(new FileInputStream(PROPERTIES_FILE));

        // ændre disse til en statisk værdi hvis man vil bygge .apk til android
        // github fatter ikke så meget af config filen (gitignore), da den også kører i et workflow
        dataSource = new SQLServerDataSource();
        dataSource.setServerName(props.getProperty("Server"));
        dataSource.setDatabaseName(props.getProperty("Database"));
        dataSource.setUser(props.getProperty("User"));
        dataSource.setPassword(props.getProperty("Password"));
        dataSource.setPortNumber(1433);
        dataSource.setTrustServerCertificate(true);
    }

    @Override
    public Connection getConnection() throws SQLServerException {
        return dataSource.getConnection();
    }
}
