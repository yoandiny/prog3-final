package mg.yoan.finaltd.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final Dotenv DOTENV = Dotenv.load();

    private static final String URL = getEnv("DB_URL");
    private static final String USER = getEnv("DB_USER");
    private static final String PASSWORD = getEnv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Successfully connected to the database!");
        return connection;
    }

    private static String getEnv(String key) {
        String value = DOTENV.get(key);
        if (value == null) {
            value = System.getenv(key);
        }
        return value;
    }
}
