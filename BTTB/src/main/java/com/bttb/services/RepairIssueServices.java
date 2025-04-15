/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bttb.services;

import com.bttb.pojo.RepairIssue;
import com.bttb.pojo.JdbcUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepairIssueServices {

    public List<RepairIssue> getRepairIssues() throws SQLException {
        List<RepairIssue> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM repair_issue";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                RepairIssue ri = new RepairIssue(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("device_type_id"),
                        rs.getDouble("cost")
                );
                list.add(ri);
            }
        }

        return list;
    }

    public List<RepairIssue> getRepairIssuesByDeviceId(String deviceId) throws SQLException {
        List<RepairIssue> issues = new ArrayList<>();

        String sql = "SELECT ri.id, ri.name, ri.cost, ri.device_type_id FROM device d "
                + "JOIN repair_issue ri ON d.device_type_id = ri.device_type_id "
                + "WHERE d.id = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, deviceId);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                RepairIssue issue = new RepairIssue(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("device_type_id"),
                        rs.getDouble("cost")
                );
                issues.add(issue);
            }
        }

        return issues;
    }
}
