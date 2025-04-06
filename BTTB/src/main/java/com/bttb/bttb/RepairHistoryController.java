package com.bttb.bttb;

import com.bttb.pojo.JdbcUtils;
import com.bttb.pojo.RepairHistory;
import com.bttb.services.DeviceServices;
import com.bttb.services.RepairHistoryServices;
import com.bttb.services.UserServices;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class RepairHistoryController implements Initializable {

    @FXML
    private TableView<RepairHistory> tableRepairHistory;
    @FXML
    private TableColumn<RepairHistory, Integer> colId;
    @FXML
    private TableColumn<RepairHistory, Integer> colDeviceId;
    @FXML
    private TableColumn<RepairHistory, String> colTechnician;
    @FXML
    private TableColumn<RepairHistory, String> colRepairDate;
    @FXML
    private TableColumn<RepairHistory, String> colCompletionDate;
    @FXML
    private TableColumn<RepairHistory, Double> colCost;
    @FXML
    private TableColumn<RepairHistory, String> colStatus;
    @FXML
    private TableColumn<RepairHistory, Void> colAction;
    @FXML
    private ComboBox<String> addTechnicianComboBox;
    @FXML
    private ComboBox<String> findTechnicianComboBox;
    @FXML
    private ComboBox<String> addDeviceComboBox;
    @FXML
    private ComboBox<String> findDeviceComboBox;
    @FXML
    private DatePicker addRepairDatePicker;
    @FXML
    private DatePicker findStartDatePicker;
    @FXML
    private DatePicker findEndDatePicker;

// Các cột khác
    // Ngày hoàn thành
    private final RepairHistoryServices repairHistoryService = new RepairHistoryServices();
    private final UserServices userServices = new UserServices();
    private final DeviceServices deviceServices = new DeviceServices();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadRepairHistoryData();
        loadTechnicians();
        loadDevices();
    }

    private void loadTechnicians() {
        try {
            addTechnicianComboBox.setItems(FXCollections.observableArrayList(userServices.getTechnicians()));
            addTechnicianComboBox.setEditable(true); // Cho phép vừa nhập vừa chọn
            findTechnicianComboBox.setItems(FXCollections.observableArrayList(userServices.getTechnicians()));
            findTechnicianComboBox.setEditable(true); // Cho phép vừa nhập vừa chọn

        } catch (SQLException e) {
            showError("Lỗi khi tải danh sách kỹ thuật viên: " + e.getMessage());
        }
    }

    private void loadDevices() {
        try {
            List<String> deviceIds = deviceServices.getBrokenDevices(); // Không gọi trực tiếp từ lớp
            ObservableList<String> deviceList = FXCollections.observableArrayList(deviceIds);

            addDeviceComboBox.setItems(deviceList);
            addDeviceComboBox.setEditable(true);
            findDeviceComboBox.setItems(deviceList);
            findDeviceComboBox.setEditable(true);

        } catch (SQLException e) {
            showError("Lỗi khi tải danh sách thiết bị: " + e.getMessage());
        }

    }

    private void setupTableColumns() {

        colDeviceId.setCellValueFactory(new PropertyValueFactory<>("deviceId"));
        colTechnician.setCellValueFactory(new PropertyValueFactory<>("technician"));
        colRepairDate.setCellValueFactory(new PropertyValueFactory<>("repairDate"));
        colCompletionDate.setCellValueFactory(new PropertyValueFactory<>("CompletionDate"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Cột chứa nút "Hoàn Thành" và "Xóa"
        colAction.setCellFactory(param -> new TableCell<RepairHistory, Void>() {
            private final Button btnComplete = new Button("Hoàn Thành");
            private final Button btnDelete = new Button("Xóa");

            {
                // Xử lý nút "Hoàn Thành"
                btnComplete.setOnAction(event -> {
                    RepairHistory repair = getTableView().getItems().get(getIndex());
                    markAsCompleted(repair);
                });

                // Xử lý nút "Xóa"
                btnDelete.setOnAction(event -> {
                    RepairHistory repair = getTableView().getItems().get(getIndex());
                    deleteRepairHistory(repair);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // Đảm bảo không có cột trống sau nút
                    HBox hbox = new HBox(10, btnComplete, btnDelete); // Hiển thị cả 2 nút
                    setGraphic(hbox);
                    // Disable nút "Hoàn Thành" nếu trạng thái là "Đã hoàn thành"
                    btnComplete.setDisable("Đã hoàn thành".equals(getTableView().getItems().get(getIndex()).getStatus()));
                }
            }
        });
    }

    private void deleteRepairHistory(RepairHistory repair) {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "DELETE FROM repair_history WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, repair.getId());
            stm.executeUpdate();

            // Xóa sửa chữa khỏi danh sách và làm mới TableView
            tableRepairHistory.getItems().remove(repair);
            tableRepairHistory.refresh();
        } catch (SQLException e) {
            showError("Lỗi khi xóa dữ liệu: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveRepairHistory() {
        // Lấy dữ liệu từ các trường nhập liệu
        String technician = addTechnicianComboBox.getValue();
        String deviceId = addDeviceComboBox.getValue();
        String repairDate = addRepairDatePicker.getValue() != null ? addRepairDatePicker.getValue().toString() : null;

        // Kiểm tra nếu tất cả các trường đều có dữ liệu hợp lệ
        if (technician == null || technician.isEmpty() || deviceId == null || repairDate == null) {
            showError("Vui lòng điền đầy đủ thông tin.");
            return;
        }

        // Chuyển đổi ngày từ String sang LocalDateTime nếu cần
        LocalDateTime repairDateTime = LocalDateTime.parse(repairDate + "T00:00:00"); // Ví dụ: chỉ lấy ngày, không có thời gian
        if (isTechnicianBusy(technician)) {
            showError("Kỹ thuật viên này đang có lịch sửa chữa chưa hoàn thành. Vui lòng chọn kỹ thuật viên khác.");
            return;
        }
        // Tạo đối tượng RepairHistory mới
        RepairHistory newRepair = new RepairHistory(
                0, // 
                Integer.parseInt(deviceId),
                technician,
                repairDateTime, // repairDate
                null,
                "Chưa hoàn thành", // status
                0.0 // cost
        );

        // Lưu vào cơ sở dữ liệu
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "INSERT INTO repair_history (technician, device_id, repair_date, status, cost) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, newRepair.getTechnician());
            stm.setInt(2, newRepair.getDeviceId());
            stm.setObject(3, newRepair.getRepairDate()); // LocalDateTime
            stm.setString(4, newRepair.getStatus()); // Chắc chắn dùng giá trị status đúng
            System.out.println("Status: " + newRepair.getStatus());
            stm.setDouble(5, newRepair.getCost());
            stm.executeUpdate();

            // Làm mới lại dữ liệu TableView sau khi thêm
            loadRepairHistoryData();  // Load lại danh sách lịch sử sửa chữa

        } catch (SQLException e) {
            showError("Lỗi khi lưu lịch sử sửa chữa: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearchRepairHistory() {
        // Lấy dữ liệu từ các ComboBox và DatePicker
        String technician = findTechnicianComboBox.getValue();
        String deviceId = findDeviceComboBox.getValue();
        LocalDateTime startDate = findStartDatePicker.getValue() != null ? findStartDatePicker.getValue().atStartOfDay() : null;
        LocalDateTime endDate = findEndDatePicker.getValue() != null ? findEndDatePicker.getValue().atTime(23, 59, 59) : null;
        System.out.println("Parameters: " + technician + ", " + deviceId + ", " + startDate + ", " + endDate);

        try {
            // Sử dụng RepairHistoryServices để tìm kiếm với các tiêu chí
            List<RepairHistory> searchResults = repairHistoryService.searchRepairHistory(technician, deviceId, startDate, endDate);

            // Cập nhật dữ liệu vào bảng
            tableRepairHistory.setItems(FXCollections.observableList(searchResults));

            // Nếu không tìm thấy kết quả, thông báo cho người dùng
            if (searchResults.isEmpty()) {
                showError("Không có kết quả tìm kiếm khớp.");
            }
        } catch (SQLException e) {
            showError("Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    private void markAsCompleted(RepairHistory repair) {
        if (!"Đã hoàn thành".equals(repair.getStatus())) {
            try (Connection conn = JdbcUtils.getConn()) {
                String sql = "UPDATE repair_history SET status = ?, completion_date = ? WHERE id = ?";
                PreparedStatement stm = conn.prepareStatement(sql);

                LocalDateTime now = LocalDateTime.now(); // Lấy thời gian hiện tại
                stm.setString(1, "Đã hoàn thành");
                stm.setTimestamp(2, Timestamp.valueOf(now));
                stm.setInt(3, repair.getId());

                int rowsUpdated = stm.executeUpdate();
                if (rowsUpdated > 0) {
                    // Cập nhật lại dữ liệu trong đối tượng RepairHistory
                    repair.setStatus("Đã hoàn thành");
                    repair.setCompletionDate(now);

                    // Cập nhật giao diện
                    loadRepairHistoryData(); // Load lại danh sách thay vì chỉ refresh
                }
            } catch (SQLException e) {
                showError("Lỗi cập nhật: " + e.getMessage());
            }
        }
    }

    private void loadRepairHistoryData() {
        try {
            tableRepairHistory.setItems(FXCollections.observableList(repairHistoryService.getRepairHistories()));
        } catch (SQLException e) {
            showError("Lỗi khi tải dữ liệu lịch sửa chữa: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isTechnicianBusy(String technician) {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT COUNT(*) FROM repair_history WHERE technician = ? AND status = 'Chưa hoàn thành'";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, technician);
            ResultSet rs = stm.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true; // Kỹ thuật viên đang có lịch sửa chữa chưa hoàn thành
            }
        } catch (SQLException e) {
            showError("Lỗi kiểm tra lịch sửa chữa: " + e.getMessage());
        }
        return false; // Kỹ thuật viên không bận
    }

}
