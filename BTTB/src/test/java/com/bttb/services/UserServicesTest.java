package com.bttb.services;

import com.bttb.pojo.JdbcUtils;
import com.bttb.pojo.Users;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServicesTest {

    private static UsersServices userServices;

    @BeforeAll
    public static void setUpClass() throws Exception {
        userServices = new UsersServices();
        createUserTableIfNotExists();
    }

    @BeforeEach
    public void resetDatabase() throws SQLException {
        try (Connection conn = JdbcUtils.getConn(); Statement stmt = conn.createStatement()) {
            // Xóa toàn bộ dữ liệu users
            stmt.execute("DELETE FROM users");

            // Reset auto increment nếu cần (H2 SQL)
            stmt.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");

            // Thêm user mẫu
            stmt.execute("INSERT INTO users (name, email, username, password, role) "
                    + "VALUES ('KTV 1', 'ktv1@gmail.com', 'tech1', '123456', 'technician')");
        }
    }

    private static void createUserTableIfNotExists() throws SQLException {
        try (Connection conn = JdbcUtils.getConn(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "name VARCHAR(100), "
                    + "email VARCHAR(100), "
                    + "username VARCHAR(50) UNIQUE, "
                    + "password VARCHAR(100), "
                    + "role VARCHAR(50))");
        }
    }

    @Test
    public void testGetTechnicians() throws SQLException {
        List<String> technicians = userServices.getTechnicians();
        assertNotNull(technicians, "Danh sách kỹ thuật viên không được null");
        assertTrue(technicians.contains("KTV 1"), "Phải chứa KTV mẫu");
    }

    @Test
    public void testGetTechniciansListUser() throws SQLException {
        List<Users> techUsers = userServices.getTechniciansListUsers();
        assertNotNull(techUsers, "Danh sách kỹ thuật viên không được null");
        assertFalse(techUsers.isEmpty(), "Phải có ít nhất một kỹ thuật viên");

        for (Users u : techUsers) {
            assertNotNull(u.getName(), "Tên kỹ thuật viên không được null");
            assertTrue(u.getId() > 0, "ID kỹ thuật viên phải lớn hơn 0");
        }
    }

    @Test
    public void testGetUserByUsernameAndRole_Valid() throws SQLException {
        Users u = userServices.getUsersByUsernameAndRole("tech1", "technician");
        assertNotNull(u, "Phải trả về user nếu tồn tại");
        assertEquals("technician", u.getRole());
        assertEquals("tech1", u.getUsername());
    }

    @Test
    public void testGetUserByUsernameAndRole_Invalid() throws SQLException {
        Users u = userServices.getUsersByUsernameAndRole("nonexistent_user", "technician");
        assertNull(u, "Phải trả về null nếu user không tồn tại");
    }

    @Test
    public void testAddUser_Valid() throws SQLException {
        String uniqueUsername = "testuser_" + System.currentTimeMillis();

        Users u = new Users();
        u.setName("Test User");
        u.setEmail("testuser@example.com");
        u.setUsername(uniqueUsername);
        u.setPassword("123456");
        u.setRole("technician");

        boolean result = userServices.addUser(u);
        assertTrue(result, "Thêm user hợp lệ phải trả về true");
    }

    @Test
    public void testAddUser_DuplicateUsername() throws SQLException {
        String duplicateUsername = "dupuser_" + System.currentTimeMillis();

        // Bước 1: Thêm user đầu tiên
        Users existingUser = new Users();
        existingUser.setName("Original User");
        existingUser.setEmail("original@example.com");
        existingUser.setUsername(duplicateUsername);
        existingUser.setPassword("123456");
        existingUser.setRole("technician");

        boolean firstAdd = userServices.addUser(existingUser);
        assertTrue(firstAdd, "Thêm user đầu tiên phải thành công");

        // Bước 2: Thử thêm user trùng username
        Users duplicateUser = new Users();
        duplicateUser.setName("Duplicate User");
        duplicateUser.setEmail("dup@example.com");
        duplicateUser.setUsername(duplicateUsername);
        duplicateUser.setPassword("123456");
        duplicateUser.setRole("technician");

        boolean secondAdd = userServices.addUser(duplicateUser);
        assertFalse(secondAdd, "Thêm user trùng username nên trả về false");
    }
}
