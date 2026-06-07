package com.example.jdbcjframe.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public final class DBConnection {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/jdbc_jframe_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";
    private static final String PROPERTIES_FILE = "/db.properties";

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        Properties properties = loadProperties();
        String url = properties.getProperty("db.url", DEFAULT_URL);
        String user = properties.getProperty("db.user", DEFAULT_USER);
        String password = properties.getProperty("db.password", DEFAULT_PASSWORD);

        Connection connection = DriverManager.getConnection(url, user, password);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS students (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(150) NOT NULL UNIQUE, " +
                "course VARCHAR(100) NOT NULL)"
            );
        }
        return connection;
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = DBConnection.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database properties", e);
        }

        properties.put("db.url", System.getProperty("db.url", System.getenv().getOrDefault("DB_URL", properties.getProperty("db.url", DEFAULT_URL))));
        properties.put("db.user", System.getProperty("db.user", System.getenv().getOrDefault("DB_USER", properties.getProperty("db.user", DEFAULT_USER))));
        properties.put("db.password", System.getProperty("db.password", System.getenv().getOrDefault("DB_PASSWORD", properties.getProperty("db.password", DEFAULT_PASSWORD))));

        return properties;
    }
}
