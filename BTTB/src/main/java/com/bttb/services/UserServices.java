package com.bttb.services;

import com.bttb.pojo.JdbcUtils;
import com.bttb.pojo.User;
import com.bttb.services.HashUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserServices {

    public List<String> getTechnicians() throws SQLException {
        List<String> technicians = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            // Cập nhật truy vấn để lấy tên kỹ thuật viên thay vì chỉ lấy id
            String sql = "SELECT id FROM user WHERE role = 'technician'";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                // Cộng thêm tên kỹ thuật viên vào danh sách
                String technician = rs.getString("id"); // Giả sử 'name' là tên của kỹ thuật viên
                technicians.add(technician);
            }
        }
        return technicians;
    }

    public List<User> getTechniciansListUser() throws SQLException {
        List<User> technicians = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT id, name FROM user WHERE role = 'technician'";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                technicians.add(new User(id, name));
            }
        }
        return technicians;
    }

    public User getUserByUsernameAndRole(String username, String role) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE username = ? AND role = ?");
        stmt.setString(1, username);
        stmt.setString(2, role);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new User(
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

    public boolean addUser(User u) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "INSERT INTO user(name, email, username, password, role) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, u.getName());
            stm.setString(2, u.getEmail());
            stm.setString(3, u.getUsername());
            String hashedPassword = HashUtils.hashPassword(u.getPassword());
            stm.setString(4, hashedPassword); // sau này có thể hash ở đây
            stm.setString(5, u.getRole());

            return stm.executeUpdate() > 0;
        }

    }

}
