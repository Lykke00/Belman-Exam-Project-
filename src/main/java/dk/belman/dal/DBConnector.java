package dk.belman.dal;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;

public class DBConnector implements IDBConnector {
    //designate file path for db settings
    //private static final String PROPERTIES_FILE = "config/config.settings";
    private final SQLServerDataSource dataSource;

    public DBConnector() throws IOException {
        //load db settings to properties
    //    Properties props = new Properties();
    //    props.load(new FileInputStream(PROPERTIES_FILE));

        //dataSource configured with database connection details
        dataSource = new SQLServerDataSource();
        dataSource.setServerName("86.52.144.128");
        dataSource.setDatabaseName("belsign");
        dataSource.setUser("lykke");
        dataSource.setPassword("elskerpatrick");
        dataSource.setPortNumber(1433);
        dataSource.setTrustServerCertificate(true);
    }

    @Override
    public Connection getConnection() throws SQLServerException {
        return dataSource.getConnection();
    }
}
