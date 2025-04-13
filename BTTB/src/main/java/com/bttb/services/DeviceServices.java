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
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author nhanh
 */
public class DeviceServices {

    private Device_managementController controller;

    // Constructor để truyền controller từ bên ngoài
    public DeviceServices(Device_managementController controller) {
        this.controller = controller;
    }

    public DeviceServices() {
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

    public List<Device> getAllDevicesForSearch() {
        List<Device> devices = new ArrayList<>();
        String query = "SELECT d.id, d.name, d.status, dt.name as deviceTypeName "
                + "FROM device d JOIN device_type dt ON d.device_type_id = dt.id";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String status = rs.getString("status");
                String deviceTypeName = rs.getString("deviceTypeName");

                devices.add(new Device(id, name, status, deviceTypeName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return devices;
    }

    public List<Device> getAllDevices() throws SQLException {
        List<Device> list = new ArrayList<>();
        Connection conn = JdbcUtils.getConn();

        String sql = "SELECT d.id, d.name, d.status, dt.name AS device_type_name "
                + "FROM device d "
                + "JOIN device_type dt ON d.device_type_id = dt.id";

        PreparedStatement stm = conn.prepareStatement(sql);
        ResultSet rs = stm.executeQuery();

        while (rs.next()) {
            Device d = new Device();
            d.setId(rs.getInt("id"));
            d.setName(rs.getString("name"));
            d.setStatus(rs.getString("status"));
            d.setDeviceTypeName(rs.getString("device_type_name"));

            list.add(d);
        }

        return list;
    }

    public boolean addDevice(Device device) {
        String sql = "INSERT INTO device(name, status, device_type_id) VALUES (?, ?, ?)";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, device.getName());
            stmt.setString(2, device.getStatus());
            stmt.setInt(3, device.getDevice_type_id());
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                controller.loadDeviceData();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Device> getDevicesForRepair() throws SQLException {
        List<Device> devices = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM device";
            PreparedStatement stm = conn.prepareCall(sql);

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Device d = new Device(rs.getInt("id"), rs.getString("name"), rs.getString("status"), rs.getInt("device_type_id"));
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
    public List<Device> getBrokenDevices() throws SQLException {
        List<Device> devices = new ArrayList<>();

        Connection conn = JdbcUtils.getConn();
        String sql = "SELECT id, name, status FROM device WHERE status = 'Hỏng hóc'";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Device d = new Device(rs.getInt("id"), rs.getString("name"), rs.getString("status"));
            devices.add(d);
        }

        return devices;
    }

    public Device getDeviceById(int id) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        String sql = "SELECT * FROM device WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id); // ⚠️ thêm dòng này để gán giá trị id

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return new Device(rs.getInt("id"), rs.getString("name"), rs.getString("status"));
        }

        return null; // nếu không tìm thấy
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
