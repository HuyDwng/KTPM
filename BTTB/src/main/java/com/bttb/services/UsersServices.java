package com.bttb.services;

import com.bttb.pojo.JdbcUtils;
import com.bttb.pojo.Users;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsersServices {

    public List<String> getTechnicians() throws SQLException {
        List<String> technicians = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT name FROM users WHERE role = 'technician'";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                String technicianName = rs.getString("name"); // Đúng: lấy tên
                technicians.add(technicianName);
            }
        }
        return technicians;
    }

    public List<Users> getTechniciansListUsers() throws SQLException {
        List<Users> technicians = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT id, name FROM users WHERE role = 'technician'";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                technicians.add(new Users(id, name));
            }
        }
        return technicians;
    }

    public Users getUsersByUsernameAndRole(String username, String role) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND role = ?");
        stmt.setString(1, username);
        stmt.setString(2, role);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new Users(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
            );
        }

        return null;
    }

    public boolean addUser(Users u) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            // Kiểm tra trùng username
            String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement checkStm = conn.prepareStatement(checkSql);
            checkStm.setString(1, u.getUsername());
            ResultSet rs = checkStm.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Username đã tồn tại
            }

            // Thêm user nếu không trùng
            String sql = "INSERT INTO users(name, email, username, password, role) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, u.getName());
            stm.setString(2, u.getEmail());
            stm.setString(3, u.getUsername());
            String hashedPassword = HashUtils.hashPassword(u.getPassword());
            stm.setString(4, hashedPassword);
            stm.setString(5, u.getRole());

            return stm.executeUpdate() > 0;
        }
    }

}
