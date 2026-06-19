// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/subscriptionsentry_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "MySQL@2025";

    public DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/subscriptionsentry_db", "root", "MySQL@2025");
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException var1) {
            System.err.println("MySQL JDBC Driver not found. Ensure the MySQL connector JAR is added to the libraries.");
            var1.printStackTrace();
        }

    }
}
