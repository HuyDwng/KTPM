/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bttb.services;

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
     public List<String> getDeviceIds() throws SQLException {
        List<String> deviceIds = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT id FROM device"; // Giả sử bảng thiết bị có tên là 'device'
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                deviceIds.add(rs.getString("id"));
            }
        }
        return deviceIds;
    }
   
    

}