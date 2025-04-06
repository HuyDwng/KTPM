package com.bttb.services;

import com.bttb.pojo.JdbcUtils;
import com.bttb.pojo.RepairHistory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RepairHistoryServices {

    public List<RepairHistory> getRepairHistories() throws SQLException {
        List<RepairHistory> histories = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM repair_history";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                // Kiểm tra null trước khi gọi toLocalDateTime
                Timestamp repairDate = rs.getTimestamp("repair_date");
                Timestamp completionDate = rs.getTimestamp("completion_date");

                RepairHistory rh = new RepairHistory(
                        rs.getInt("id"),
                        rs.getInt("device_id"),
                        rs.getString("technician"),
                        repairDate != null ? repairDate.toLocalDateTime() : null,
                        completionDate != null ? completionDate.toLocalDateTime() : null,
                        rs.getString("status"),
                        rs.getDouble("cost")
                );
                histories.add(rh);
            }
        }
        return histories;
    }

    public List<RepairHistory> searchRepairHistory(String technician, String deviceId, LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        // Kiểm tra nếu tất cả các tham số đều trống hoặc null, thì không thực hiện gì cả
      

        // Xây dựng truy vấn SQL với các điều kiện động
        StringBuilder sql = new StringBuilder("SELECT * FROM repair_history WHERE 1=1");

        if (technician != null && !technician.isEmpty()) {
            sql.append(" AND technician = ?");
        }
        if (deviceId != null && !deviceId.isEmpty()) {
            sql.append(" AND device_id = ?");
        }
        if (startDate != null) {
            sql.append(" AND repair_date >= ?");
        }
        if (endDate != null) {
            sql.append(" AND completion_date <= ?");
        }
        
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(sql.toString());
            int index = 1;

            if (technician != null && !technician.isEmpty()) {
                stm.setString(index++, technician);
            }
            if (deviceId != null && !deviceId.isEmpty()) {
                stm.setString(index++, deviceId);
            }
            if (startDate != null) {
                stm.setTimestamp(index++, Timestamp.valueOf(startDate));
            }
            if (endDate != null) {
                stm.setTimestamp(index++, Timestamp.valueOf(endDate));
            }

            System.out.println("Executing query: " + sql.toString());  // Kiểm tra câu truy vấn

            ResultSet rs = stm.executeQuery();
            List<RepairHistory> repairHistories = new ArrayList<>();

            while (rs.next()) {
                RepairHistory repairHistory = new RepairHistory(
                        rs.getInt("id"),
                        rs.getInt("device_id"),
                        rs.getString("technician"),
                        rs.getTimestamp("repair_date").toLocalDateTime(),
                        rs.getTimestamp("completion_date") != null ? rs.getTimestamp("completion_date").toLocalDateTime() : null,
                        rs.getString("status"),
                        rs.getDouble("cost")
                );
                repairHistories.add(repairHistory);
            }

            if (repairHistories.isEmpty()) {
                System.out.println("No results found.");
            }

            return repairHistories;
        }
    }

}
