//import com.bttb.pojo.User;
//import com.bttb.services.UserServices;
//import org.junit.jupiter.api.*;
//import java.sql.SQLException;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class UserServicesTest {
//    private static UserServices userServices;
//
//    @BeforeAll
//    public static void setUpClass() {
//        userServices = new UserServices();
//    }
//
//    @Test
//    public void testGetTechnicians() throws SQLException {
//        List<String> technicians = userServices.getTechnicians();
//        assertNotNull(technicians, "Danh sách kỹ thuật viên không được null");
//        // Có thể kiểm tra nếu biết chắc có ít nhất một kỹ thuật viên
//        // assertTrue(technicians.size() > 0, "Phải có ít nhất 1 kỹ thuật viên");
//    }
//
//    @Test
//    public void testGetTechniciansListUser() throws SQLException {
//        List<User> techUsers = userServices.getTechniciansListUser();
//        assertNotNull(techUsers, "Danh sách kỹ thuật viên không được null");
//        if (!techUsers.isEmpty()) {
//            for (User u : techUsers) {
//                assertNotNull(u.getName(), "Tên kỹ thuật viên không được null");
//                assertTrue(u.getId() > 0, "ID kỹ thuật viên phải lớn hơn 0");
//            }
//        }
//    }
//
//    @Test
//    public void testGetUserByUsernameAndRole_Valid() throws SQLException {
//        // Cần có trước user trong DB với role = 'technician'
//        User u = userServices.getUserByUsernameAndRole("tech1", "technician");
//        assertNotNull(u, "Phải trả về user nếu tồn tại");
//        assertEquals("technician", u.getRole());
//    }
//
//    @Test
//    public void testGetUserByUsernameAndRole_Invalid() throws SQLException {
//        User u = userServices.getUserByUsernameAndRole("nonexistent_user", "technician");
//        assertNull(u, "Phải trả về null nếu user không tồn tại");
//    }
//
//    @Test
//    public void testAddUser_Valid() throws SQLException {
//        User u = new User();
//        u.setName("Test User");
//        u.setEmail("testuser@example.com");
//        u.setUsername("testuser123");
//        u.setPassword("123456");
//        u.setRole("technician");
//
//        boolean result = userServices.addUser(u);
//        assertTrue(result, "Thêm user hợp lệ phải trả về true");
//    }
//
//    @Test
//    public void testAddUser_DuplicateUsername() throws SQLException {
//        User u = new User();
//        u.setName("Duplicate User");
//        u.setEmail("dup@example.com");
//        u.setUsername("testuser123"); // Username đã tồn tại
//        u.setPassword("123456");
//        u.setRole("technician");
//
//        boolean result = userServices.addUser(u);
//        assertFalse(result, "Thêm user trùng username nên trả về false");
//    }
//}
