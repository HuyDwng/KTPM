//package com.bttb.services;
//
//import com.bttb.pojo.Device;
//import com.bttb.pojo.JdbcUtils;
//import com.bttb.pojo.MaintenanceSchedule;
//import com.bttb.pojo.User;
//import org.junit.jupiter.api.*;
//
//import java.sql.Connection;
//import java.sql.Statement;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class ScheduleServicesTest {
//
//    private ScheduleServices scheduleServices;
//
//    @BeforeAll
//    public void setupDatabase() throws Exception {
//        System.setProperty("test.mode", "true");
//        scheduleServices = new ScheduleServices();
//
//        try (Connection conn = JdbcUtils.getConn(); Statement stmt = conn.createStatement()) {
//            // Tạo bảng users và device
//            stmt.execute("CREATE TABLE users (" +
//                    "id INT PRIMARY KEY," +
//                    "name VARCHAR(100)," +
//                    "email VARCHAR(100)," +
//                    "role VARCHAR(50))");
//
//            stmt.execute("CREATE TABLE device (" +
//                    "id INT PRIMARY KEY," +
//                    "name VARCHAR(100)," +
//                    "status VARCHAR(50))");
//
//            // Tạo bảng maintenance_schedule
//            stmt.execute("CREATE TABLE maintenance_schedule (" +
//                    "id INT AUTO_INCREMENT PRIMARY KEY," +
//                    "device_id INT," +
//                    "scheduled_date DATE," +
//                    "scheduled_time TIME," +
//                    "frequency VARCHAR(50)," +
//                    "executor_id INT," +
//                    "maintenance_period DATE," +
//                    "created_at DATE," +
//                    "completed_date DATE," +
//                    "last_maintenance_date DATE," +
//                    "next_maintenance_date DATE)");
//
//            // Thêm dữ liệu mẫu
//            stmt.execute("INSERT INTO users (id, name, email, role) VALUES (100, 'KTV A', 'ktva@gmail.com', 'technician')");
//            stmt.execute("INSERT INTO device (id, name, status) VALUES (1, 'Thiết bị A', 'Đang hoạt động')");
//            stmt.execute("INSERT INTO device (id, name, status) VALUES (2, 'Thiết bị B', 'Không hoạt động')");
//
//            stmt.execute("INSERT INTO maintenance_schedule (" +
//                    "device_id, scheduled_date, scheduled_time, frequency, executor_id, maintenance_period) " +
//                    "VALUES (1, '2025-04-17', '10:00:00', 'hàng tuần', 100, '2025-04-24')");
//        }
//    }
//
//    @Test
//    public void testIsScheduleDuplicate_True() throws Exception {
//        assertTrue(scheduleServices.isScheduleDuplicate(1));
//    }
//
//    @Test
//    public void testIsScheduleDuplicate_False() throws Exception {
//        assertFalse(scheduleServices.isScheduleDuplicate(999));
//    }
//
//    @Test
//    public void testAddMaintenanceSchedule() throws Exception {
//        boolean result = scheduleServices.addMaintenanceSchedule(
//                1,
//                LocalDate.of(2025, 4, 20),
//                LocalTime.of(14, 0),
//                "hàng tháng",
//                100,
//                LocalDate.of(2025, 5, 20)
//        );
//        assertTrue(result);
//    }
//
//    @Test
//    public void testGetActiveDevices() throws Exception {
//        List<Device> activeDevices = scheduleServices.getActiveDevices();
//        assertEquals(1, activeDevices.size());
//        assertEquals("Thiết bị A", activeDevices.get(0).getName());
//    }
//
//    @Test
//    public void testLoadExecutors() {
//        List<User> executors = ScheduleServices.loadExecutors();
//        assertEquals(1, executors.size());
//        assertEquals("KTV A", executors.get(0).getName());
//    }
//
//    @Test
//    public void testGetExecutorEmail() {
//        String email = ScheduleServices.getExecutorEmail(100);
//        assertEquals("ktva@gmail.com", email);
//    }
//
//    @Test
//    public void testGetAllSchedules() throws Exception {
//        List<MaintenanceSchedule> list = scheduleServices.getAllSchedules();
//        assertTrue(list.size() >= 1);
//        assertEquals("Thiết bị A", list.get(0).getDeviceName());
//    }
//
//    @Test
//    public void testCompleteSchedule() {
//        boolean result = scheduleServices.completeSchedule(1, LocalDate.of(2025, 4, 17), "hàng tuần");
//        assertTrue(result);
//    }
//
//    @Test
//    public void testMarkAsCompleted() throws Exception {
//        boolean result = scheduleServices.markAsCompleted(1, LocalDate.of(2025, 4, 17));
//        assertTrue(result);
//    }
//
//    @Test
//    public void testDeleteSchedule() throws Exception {
//        // Thêm 1 lịch mới trước khi xóa
//        scheduleServices.addMaintenanceSchedule(
//                1,
//                LocalDate.of(2025, 4, 25),
//                LocalTime.of(11, 0),
//                "hàng tháng",
//                100,
//                LocalDate.of(2025, 5, 25)
//        );
//        List<MaintenanceSchedule> schedules = scheduleServices.getAllSchedules();
//        int lastId = schedules.get(schedules.size() - 1).getId();
//
//        boolean deleted = scheduleServices.deleteSchedule(lastId);
//        assertTrue(deleted);
//    }
//
//    @AfterAll
//    public void teardownDatabase() throws Exception {
//        try (Connection conn = JdbcUtils.getConn(); Statement stmt = conn.createStatement()) {
//            stmt.execute("DROP ALL OBJECTS"); // H2 hỗ trợ reset DB nhanh
//        }
//    }
//}
