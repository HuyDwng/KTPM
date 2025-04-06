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

}
