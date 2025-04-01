package com.bttb.services;

import com.bttb.pojo.Device;
import com.bttb.pojo.JdbcUtils;
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
import java.util.ArrayList;
import java.util.List;
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

    // 🔹 Lấy danh sách thiết bị từ database
    public ObservableList<Device> getDevices() throws SQLException {
        List<Device> devices = new ArrayList<>();
        String query = "SELECT * FROM device";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(query); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                Device d = new Device();
                d.setId(rs.getInt("id"));
                d.setName(rs.getString("name"));
                d.setStatus(rs.getString("status"));
                devices.add(d);
            }
        }
        return FXCollections.observableArrayList(devices);
    }

    // 🔹 Lọc danh sách thiết bị có trạng thái "Đang hoạt động"
    public ObservableList<Device> getActiveDevices() throws SQLException {
        return FXCollections.observableArrayList(
                getDevices().stream()
                        .filter(d -> "Đang hoạt động".equals(d.getStatus()))
                        .collect(Collectors.toList())
        );
    }

    // 🔹 Thêm lịch bảo trì mới vào database
    public boolean addMaintenanceSchedule(int deviceId, LocalDate scheduleDate, LocalTime scheduleTime) throws SQLException {
        String query = "INSERT INTO maintenance_schedule (device_id, scheduled_date, scheduled_time) VALUES (?, ?, ?)";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, deviceId);
            stm.setDate(2, Date.valueOf(scheduleDate));
            stm.setTime(3, Time.valueOf(scheduleTime));
            return stm.executeUpdate() > 0; // Trả về true nếu thêm thành công
        }
    }
}
