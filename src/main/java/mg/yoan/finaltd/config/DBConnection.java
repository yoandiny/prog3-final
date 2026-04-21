package mg.yoan.finaltd.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Successfully connected to the database!");
        return connection;
    }
}
