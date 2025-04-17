package com.bttb.services;

import com.bttb.pojo.Device;
import com.bttb.pojo.JdbcUtils;
import com.bttb.pojo.MaintenanceSchedule;
import com.bttb.pojo.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ScheduleServices {

    // 🔹 Kiểm tra trùng lịch bảo trì
    public boolean isScheduleDuplicate(int deviceId) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        String sql = "SELECT * FROM maintenance_schedule WHERE device_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, deviceId);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    // 🔹 Lọc danh sách thiết bị có trạng thái "Đang hoạt động"
    public ObservableList<Device> getActiveDevices() throws SQLException {
        DeviceServices DS = new DeviceServices();
        return FXCollections.observableArrayList(
                DS.getDevices().stream()
                        .filter(d -> "Đang hoạt động".equals(d.getStatus()))
                        .collect(Collectors.toList())
        );
    }

    // Phương thức load danh sách người thực hiện là kỹ thuật viên từ cơ sở dữ liệu
    public static ObservableList<User> loadExecutors() {
        ObservableList<User> executors = FXCollections.observableArrayList();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT id, name FROM \"user\" WHERE role = 'technician'";
            PreparedStatement stm = conn.prepareCall(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                executors.add(new User(rs.getInt("id"), rs.getString("name")));
            }

        } catch (SQLException e) {
            System.err.println("Error loading executors: " + e.getMessage());
        }

        return executors;
    }

    // Thêm lịch bảo trì mới vào database
    public boolean addMaintenanceSchedule(int deviceId, LocalDate scheduleDate, LocalTime scheduleTime, String frequency, int executorId, LocalDate nextMaintenanceDate) throws SQLException {
        String query = "INSERT INTO maintenance_schedule (device_id, scheduled_date, scheduled_time, frequency, executor_id, maintenance_period) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, deviceId);
            stm.setDate(2, Date.valueOf(scheduleDate));
            stm.setTime(3, Time.valueOf(scheduleTime));
            stm.setString(4, frequency);
            stm.setInt(5, executorId);
            stm.setDate(6, Date.valueOf(nextMaintenanceDate));
            return stm.executeUpdate() > 0;
        }
    }

    public static String getExecutorEmail(int executorId) {
        String email = null;
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT email FROM \"user\" WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, executorId);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                email = rs.getString("email");
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving executor email: " + e.getMessage());
        }

        return email;
    }

    public ObservableList<MaintenanceSchedule> getAllSchedules() throws SQLException {
        ObservableList<MaintenanceSchedule> list = FXCollections.observableArrayList();

        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT ms.id, d.name AS device_name, u.name AS executor_name, "
                    + "ms.scheduled_date, ms.scheduled_time, ms.frequency, "
                    //+ "ms.next_maintenance_date, ms.created_at, ms.completed_date "
                    + "ms.maintenance_period, ms.created_at, ms.last_maintenance_date "
                    + "FROM maintenance_schedule ms "
                    + "JOIN device d ON ms.device_id = d.id "
                    + "JOIN \"user\" u ON ms.executor_id = u.id "
                    + "ORDER BY ms.id DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MaintenanceSchedule m = new MaintenanceSchedule(
                        rs.getInt("id"),
                        rs.getString("device_name"),
                        rs.getString("executor_name"),
                        rs.getDate("scheduled_date").toLocalDate(),
                        rs.getTime("scheduled_time").toLocalTime(),
                        rs.getString("frequency"),
                        rs.getDate("maintenance_period") != null ? rs.getDate("maintenance_period").toLocalDate() : null,
                        rs.getDate("created_at") != null ? rs.getDate("created_at").toLocalDate() : null,
                        rs.getDate("last_maintenance_date") != null ? rs.getDate("last_maintenance_date").toLocalDate() : null
                );

                list.add(m);
            }
        }

        return list;
    }

    public boolean deleteSchedule(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "DELETE FROM maintenance_schedule WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }

    public boolean completeSchedule(int scheduleId, LocalDate completedDate, String frequency) {
        String sql = "UPDATE maintenance_schedule SET completed_date = ?, next_maintenance_date = ? WHERE id = ?";
        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setDate(1, Date.valueOf(completedDate));

            // Tính ngày tiếp theo dựa vào frequency
            LocalDate nextDate;
            switch (frequency.toLowerCase()) {
                case "hàng tuần":
                    nextDate = completedDate.plusWeeks(1);
                    break;
                case "hàng tháng":
                    nextDate = completedDate.plusMonths(1);
                    break;
                
                default:
                    nextDate = null;
            }

            if (nextDate != null) {
                stm.setDate(2, Date.valueOf(nextDate));
            } else {
                stm.setNull(2, java.sql.Types.DATE);
            }

            stm.setInt(3, scheduleId);

            return stm.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật completed_date: " + e.getMessage());
            return false;
        }
    }
    public boolean markAsCompleted(int scheduleId, LocalDate completedDate) throws SQLException {
        String sql = "UPDATE maintenance_schedule SET last_maintenance_date = ? WHERE id = ?";
        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setDate(1, java.sql.Date.valueOf(completedDate));
            stm.setInt(2, scheduleId);
            return stm.executeUpdate() > 0;
        }
    }
}
