package com.bttb.services;

import com.bttb.pojo.Device;
import com.bttb.pojo.JdbcUtils;
import com.bttb.pojo.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ScheduleServices {

    // 🔹 Kiểm tra trùng lịch bảo trì
    public boolean isScheduleDuplicate(int deviceId, LocalDateTime scheduleTime) throws SQLException {
        String query = "SELECT COUNT(*) FROM maintenance_schedule WHERE device_id = ? AND scheduled_time = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, deviceId);
            stm.setTimestamp(2, Timestamp.valueOf(scheduleTime));

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
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
            String sql = "SELECT id, name FROM user WHERE role = 'technician'";
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
        String query = "INSERT INTO maintenance_schedule (device_id, scheduled_date, scheduled_time, frequency, executor_id, next_maintenance_date) VALUES (?, ?, ?, ?, ?, ?)";
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
            String sql = "SELECT email FROM user WHERE id = ?";
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
}
