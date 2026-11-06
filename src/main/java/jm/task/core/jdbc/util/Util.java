package jm.task.core.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Util {
    public static Connection connectMySQL(String hostName, String dbName, String userName, String password) throws SQLException {
        if (
                hostName == null || hostName.isBlank() ||
                dbName == null || dbName.isBlank() ||
                userName == null || userName.isBlank() ||
                password == null || password.isBlank()
        ) {
            throw new IllegalArgumentException("MySQL connection parameters are required");
        }

        hostName = hostName.trim();
        dbName = dbName.trim();
        userName = userName.trim();
        password = password.trim();

        String conUrl = String.format("jdbc:mysql://%s:3306/%s", hostName, dbName);

        return DriverManager.getConnection(conUrl, userName, password);
    }
}
