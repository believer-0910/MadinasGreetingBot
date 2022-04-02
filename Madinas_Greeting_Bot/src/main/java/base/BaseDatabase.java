package base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static security.Security.*;

public class BaseDatabase {
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
