/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bttb.service;

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
 * @author nhanh
 */
public class DeviceService {

    private Device_managementController controller;

    // Constructor để truyền controller từ bên ngoài
    public DeviceService(Device_managementController controller) {
        this.controller = controller;
    }

    

    public DeviceService() {
    }

    public boolean addDevice(Device device) {
        String sql = "INSERT INTO device (name, status) VALUES (?, ?)";

        try (Connection conn = JdbcUtils.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, device.getName());
            stmt.setString(2, device.getStatus());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                controller.loadDeviceData();  // Gọi phương thức load lại bảng
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Device> getAllDevices() {
        List<Device> devices = new ArrayList<>();
        String sql = "SELECT id, name, status FROM device";

        try (Connection conn = JdbcUtils.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Device device = new Device(rs.getInt("id"), rs.getString("name"), rs.getString("status"));
                devices.add(device);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return devices;
    }

    public boolean updateDeviceStatus(int deviceId, String newStatus) {
        String sql = "UPDATE device SET status = ? WHERE id = ?";

        try (Connection conn = JdbcUtils.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
    try (Connection conn = JdbcUtils.getConnection(); 
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setInt(1, deviceId);
        int rowsAffected = stmt.executeUpdate();

        return rowsAffected > 0; // Nếu xóa thành công, trả về true
    } catch (SQLException e) {
        e.printStackTrace();
        return false; // Nếu có lỗi xảy ra, trả về false
    }
}
}
