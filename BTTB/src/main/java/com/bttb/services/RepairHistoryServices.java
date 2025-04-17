package com.bttb.services;

import com.bttb.pojo.Device;
import com.bttb.pojo.JdbcUtils;
import com.bttb.pojo.RepairHistory;
import com.bttb.pojo.RepairIssue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepairHistoryServices {

    private final DeviceServices deviceServices = new DeviceServices();

    public List<RepairHistory> getRepairHistories() throws SQLException {
        List<RepairHistory> histories = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            // Truy vấn lấy thông tin sửa chữa và các lỗi liên quan
            String sql = "SELECT rh.id, rh.device_id, rh.technician_id, u.name AS technician_name, "
                    + "rh.repair_date, rh.completion_date, rh.status, rh.cost, rh.device_type_id, "
                    + "ri.name AS repair_issue_name, ri.cost AS repair_issue_price "
                    + "FROM repair_history rh "
                    + "LEFT JOIN user u ON rh.technician_id = u.id "
                    + "LEFT JOIN repair_history_repair_issue rhr ON rh.id = rhr.repair_history_id "
                    + "LEFT JOIN repair_issue ri ON rhr.repair_issue_id = ri.id";

            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            Map<Integer, RepairHistory> historyMap = new HashMap<>();

            while (rs.next()) {
                int historyId = rs.getInt("id");

                RepairHistory rh = historyMap.get(historyId);
                if (rh == null) {
                    // Tạo đối tượng RepairHistory mới nếu chưa tồn tại
                    rh = new RepairHistory(
                            historyId,
                            rs.getInt("device_id"),
                            rs.getInt("technician_id"),
                            new ArrayList<>(),
                            rs.getTimestamp("repair_date") != null ? rs.getTimestamp("repair_date").toLocalDateTime() : null,
                            rs.getTimestamp("completion_date") != null ? rs.getTimestamp("completion_date").toLocalDateTime() : null,
                            rs.getString("status"),
                            rs.getDouble("cost")
                    );
                    historyMap.put(historyId, rh);
                    histories.add(rh);
                }
                rh.setTechnicianName(rs.getString("technician_name"));

                // Thêm lỗi vào danh sách repairIssues nếu có
                String repairIssueName = rs.getString("repair_issue_name");
                Double repairIssuePrice = rs.getDouble("repair_issue_price");
                if (repairIssueName != null) {
                    rh.getRepairIssue().add(repairIssueName);
                }
                Device device = deviceServices.getDeviceById(rs.getInt("device_id"));
                if (device != null) {
                    rh.setDeviceName(device.getName());
                }

                // Cộng dồn giá trị repair_issue_price vào cost
            }
        }
        return histories;
    }

    public boolean addRepairHistory(RepairHistory repairHistory, List<RepairIssue> issues) throws SQLException {
        String insertRepairSql = "INSERT INTO repair_history (technician_id, device_id, repair_date, status, cost) VALUES (?, ?, ?, ?, ?)";
        String insertIssueSql = "INSERT INTO repair_history_repair_issue (repair_history_id, repair_issue_id) VALUES (?, ?)";

        try (Connection conn = JdbcUtils.getConn()) {
            conn.setAutoCommit(false); // Bắt đầu transaction

            // Thêm repair_history
            PreparedStatement stmRepair = conn.prepareStatement(insertRepairSql, Statement.RETURN_GENERATED_KEYS);
            stmRepair.setInt(1, repairHistory.getTechnicianId());
            stmRepair.setInt(2, repairHistory.getDeviceId());
            stmRepair.setObject(3, repairHistory.getRepairDate());
            stmRepair.setString(4, repairHistory.getStatus());
            stmRepair.setDouble(5, repairHistory.getCost());
            stmRepair.executeUpdate();

            // Lấy id vừa insert
            ResultSet rs = stmRepair.getGeneratedKeys();
            int repairHistoryId = 0;
            if (rs.next()) {
                repairHistoryId = rs.getInt(1);
            } else {
                conn.rollback();
                return false;
            }

            // Thêm các lỗi vào bảng liên kết
            PreparedStatement stmIssue = conn.prepareStatement(insertIssueSql);
            for (RepairIssue issue : issues) {
                stmIssue.setInt(1, repairHistoryId);
                stmIssue.setInt(2, issue.getId());
                stmIssue.addBatch();
            }
            stmIssue.executeBatch();

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<String> getRepairIssuesForHistory(int repairHistoryId) throws SQLException {
        List<String> issues = new ArrayList<>();

        String sql
                = "SELECT i.name AS issue_name "
                + "FROM repair_history_issue rhi "
                + "JOIN repair_issue i ON rhi.repair_issue_id = i.id "
                + "WHERE rhi.repair_history_id = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, repairHistoryId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                issues.add(rs.getString("issue_name"));
            }
        }

        return issues;
    }

    public boolean isTechnicianBusy(int technicianId) {
        String sql = "SELECT COUNT(*) FROM repair_history WHERE technician_id = ? AND status = 'Đang sửa'";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setInt(1, technicianId);
            ResultSet rs = stm.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true; // Đang bận
            }

        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra kỹ thuật viên bận: " + e.getMessage());
        }

        return false; // Không bận
    }

}
