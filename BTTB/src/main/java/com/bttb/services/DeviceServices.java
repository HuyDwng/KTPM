package com.bttb.services;

import com.bttb.bttb.Device_managementController;
import com.bttb.pojo.Device;
import com.bttb.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LEGION
 */
public class DeviceServices {

    private Device_managementController controller;

    // Constructor để truyền controller từ bên ngoài
    public DeviceServices(Device_managementController controller) {
        this.controller = controller;
    }

    public DeviceServices() {
    }

    public boolean addDevice(Device device) {
        String sql = "INSERT INTO device (name, status) VALUES (?, ?)";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, device.getName());
            stmt.setString(2, device.getStatus());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                controller.loadDeviceData();  // Gọi phương thức load lại bảng
                return true;
            }
        } catch (SQLException e) {
        }
        return false;
    }

    public List<Device> getDevices() throws SQLException {
        List<Device> devices = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM device";
            PreparedStatement stm = conn.prepareCall(sql);

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Device d = new Device(rs.getInt("id"), rs.getString("name"), rs.getString("status"));
                devices.add(d);
            }

            return devices;
        }
    }

//    public List<Device> getAllDevices() {
//        List<Device> devices = new ArrayList<>();
//        String sql = "SELECT id, name, status FROM device";
//
//        try (Connection conn = JdbcUtils.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
//
//            while (rs.next()) {
//                Device device = new Device(rs.getInt("id"), rs.getString("name"), rs.getString("status"));
//                devices.add(device);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return devices;
//    }
    public List<String> getBrokenDevices() throws SQLException {
        List<String> brokenDevices = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT DISTINCT id FROM device WHERE status = 'Hỏng hóc'";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                brokenDevices.add(rs.getString("id"));
            }
        }
        return brokenDevices;
    }

    public boolean updateDeviceStatus(int deviceId, String newStatus) {
        String sql = "UPDATE device SET status = ? WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, deviceId);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteDevice(int deviceId) {
        String query = "DELETE FROM device WHERE id = ?";
        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, deviceId);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0; // Nếu xóa thành công, trả về true
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
