package com.bttb.pojo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcUtils {

    static {
        try {
            // Load cả driver MySQL và H2 (an toàn)
            Class.forName("com.mysql.cj.jdbc.Driver");
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ex) {
        }
    }

    public static Connection getConn() throws SQLException {
        String isTest = System.getProperty("test.mode");

        if ("true".equals(isTest)) {
            // 🔹 Kết nối H2 khi test
            return DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        }

        // 🔹 Kết nối MySQL khi chạy thật
        return DriverManager.getConnection("jdbc:mysql://localhost/bttbdb", "root", "huyduong2004");
    }
}
