package com.bttb.bttb;

import com.bttb.pojo.Device;
import com.bttb.pojo.JdbcUtils;
import com.bttb.pojo.RepairHistory;
import com.bttb.pojo.RepairIssue;
import com.bttb.pojo.User;
import com.bttb.services.DeviceServices;
import com.bttb.services.RepairHistoryServices;
import com.bttb.services.UserServices;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.SelectionMode;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class RepairHistoryController implements Initializable {

    @FXML
    private TableView<RepairHistory> tableRepairHistory;

    @FXML
    private TableColumn<RepairHistory, String> colDeviceName;
    @FXML
    private TableColumn<RepairHistory, String> colTechnician;
    @FXML
    private TableColumn<RepairHistory, String> colRepairIssue;
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
    private TextField txtTime;
    @FXML
    private ComboBox<User> comboBoxTechnician;

    @FXML
    private ComboBox<String> comboBoxDevice;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TableView<RepairIssue> tableIssueList;

    @FXML
    private TableColumn<RepairIssue, String> issueColumn;

    @FXML
    private TableColumn<RepairIssue, Double> costColumn;

// Các cột khác
    // Ngày hoàn thành
    private final RepairHistoryServices repairHistoryService = new RepairHistoryServices();
    private final UserServices userServices = new UserServices();
    private final DeviceServices deviceServices = new DeviceServices();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableIssueList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        System.out.println(tableIssueList);
        setupTableColumns();
        loadRepairHistoryData();
        loadTechnicians();
        loadDevices();
        setupTimeField();
        comboBoxDevice.setOnAction(event -> {
            try {
                handleDeviceSelection();
            } catch (SQLException ex) {
                Logger.getLogger(RepairHistoryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        issueColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        costColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCost()).asObject());

    }

    private void loadTechnicians() {
        try {
            List<User> techList = userServices.getTechniciansListUser();

            comboBoxTechnician.setItems(FXCollections.observableArrayList(techList));
            comboBoxTechnician.setEditable(false); // Ngăn không cho gõ

        } catch (SQLException e) {
            showError("Lỗi khi tải danh sách kỹ thuật viên: " + e.getMessage());
        }
    }

    private void handleDeviceSelection() throws SQLException {
        String selectedText = comboBoxDevice.getSelectionModel().getSelectedItem();

        // Tách ID ra khỏi chuỗi (ví dụ "6 - Máy tính - Lab 1 - Máy 02")
        int deviceId = Integer.parseInt(selectedText.split(" - ")[0]);

        // Truy vấn lại Device từ ID
        Device selectedDevice = deviceServices.getDeviceById(deviceId);
        System.out.println(selectedDevice);
        if (selectedDevice != null) {

            loadRepairIssuesForDevice(String.valueOf(deviceId));
            System.out.println(String.valueOf(deviceId));// hoặc truyền int nếu hàm hỗ trợ
        }
    }

    private void loadRepairIssuesForDevice(String deviceId) {
        System.out.println("Loading repair issues for deviceId = " + deviceId);
        ObservableList<RepairIssue> issues = FXCollections.observableArrayList();

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

            System.out.println("Issues loaded: " + issues.size());
            tableIssueList.setItems(issues);

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Lỗi khi tải danh sách lỗi: " + e.getMessage());
        }
    }

    private void loadDevices() {
        try {
            List<Device> brokenDevices = deviceServices.getBrokenDevices();

            // Chuyển sang danh sách String
            List<String> deviceStrings = brokenDevices.stream()
                    .map(Device::toString)
                    .collect(Collectors.toList());

            ObservableList<String> deviceList = FXCollections.observableArrayList(deviceStrings);

            comboBoxDevice.setItems(deviceList);
            comboBoxDevice.setEditable(true);

        } catch (SQLException e) {
            showError("Lỗi khi tải danh sách thiết bị: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        colDeviceName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        System.out.println(colDeviceName);
        colTechnician.setCellValueFactory(new PropertyValueFactory<>("technicianName"));
        System.out.println(colTechnician);
        colRepairIssue.setCellValueFactory(cellData -> {
            List<String> repairIssues = cellData.getValue().getRepairIssue();
            // Chuyển danh sách thành chuỗi, phân tách bằng dấu phẩy
            String issues = String.join(", ", repairIssues);
            return new SimpleStringProperty(issues); // Trả về chuỗi cho cột
        });
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
                    btnComplete.setDisable("Hoàn thành".equals(getTableView().getItems().get(getIndex()).getStatus()));
                }
            }
        });
    }

    private void setupTimeField() {
        txtTime.setText(":"); // Mặc định hiển thị :

        txtTime.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("^\\d{0,2}:\\d{0,2}$")) { // Chỉ cho phép nhập số vào các vị trí hợp lệ
                return change;
            }
            return null;
        }));

        txtTime.setOnKeyTyped(event -> {
            String text = txtTime.getText();
            if (text.length() < 5) { // Đảm bảo không vượt quá "HH:mm"
                int caretPos = txtTime.getCaretPosition();
                if (caretPos == 2) {
                    txtTime.positionCaret(3); // Tự động nhảy qua dấu :
                }
            }
        });

        // Khi mất focus hoặc nhấn Enter, chuẩn hóa định dạng
        txtTime.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // Khi mất focus
                formatTimeInput();
            }
        });

        txtTime.setOnAction(event -> formatTimeInput()); // Khi nhấn Enter
    }

    private void formatTimeInput() {
        String input = txtTime.getText().replace("_", "").trim();
        if (input.matches("^([01]?\\d|2[0-3]):[0-5]?\\d$")) {
            String[] parts = input.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            // Format lại thành đúng dạng HH:mm
            String formattedTime = String.format("%02d:%02d", hour, minute);
            txtTime.setText(formattedTime);
            txtTime.setStyle(""); // Xóa cảnh báo nếu có
        } else {
            // Nếu nhập sai định dạng, báo lỗi bằng viền đỏ
            txtTime.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        }
    }

    private void deleteRepairHistory(RepairHistory repair) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa?", ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Xác nhận");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = JdbcUtils.getConn()) {
                    conn.setAutoCommit(false); // Bắt đầu transaction

                    // Xóa bản ghi con trước
                    String deleteIssues = "DELETE FROM repair_history_repair_issue WHERE repair_history_id = ?";
                    try (PreparedStatement stm1 = conn.prepareStatement(deleteIssues)) {
                        stm1.setInt(1, repair.getId());
                        stm1.executeUpdate();
                    }

                    // Xóa bản ghi chính
                    String deleteRepair = "DELETE FROM repair_history WHERE id = ?";
                    try (PreparedStatement stm2 = conn.prepareStatement(deleteRepair)) {
                        stm2.setInt(1, repair.getId());
                        stm2.executeUpdate();
                    }

                    conn.commit(); // Xác nhận thay đổi

                    tableRepairHistory.getItems().remove(repair);
                } catch (SQLException e) {
                    showError("Lỗi khi xóa: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleAddRepairHistory() throws SQLException {
        // Kỹ thuật viên (User object)
        User selectedTech = comboBoxTechnician.getSelectionModel().getSelectedItem();
        int technicianId = selectedTech != null ? selectedTech.getId() : -1;
        String technicianName = selectedTech != null ? selectedTech.getName() : null;
        LocalTime selectedTime = LocalTime.parse(txtTime.getText());

        // Thiết bị (String → lấy ID → truy vấn Device)
        String selectedDeviceText = comboBoxDevice.getSelectionModel().getSelectedItem();
        if (selectedDeviceText == null || selectedDeviceText.isEmpty()) {
            showError("Vui lòng chọn thiết bị.");
            return;
        }

        int deviceId = Integer.parseInt(selectedDeviceText.split(" - ")[0]); // lấy ID từ chuỗi
        Device selectedDevice = deviceServices.getDeviceById(deviceId);
        if (selectedDevice == null) {
            showError("Không tìm thấy thông tin thiết bị.");
            return;
        }

        String deviceName = selectedDevice.getName();
        System.out.println(deviceName);
        LocalDate repairDate;
        repairDate = datePicker.getValue();
        if (technicianId == -1 || repairDate == null || selectedTime == null) {
            showError("Vui lòng điền đầy đủ thông tin.");
            return;
        }
        LocalDateTime repairDateTime = LocalDateTime.of(repairDate, selectedTime);

        if (repairDateTime.isBefore(LocalDateTime.now())) {
            showError("Thời gian lập lịch phải ở tương lai!");
            return;
        }
        // Danh sách lỗi được chọn
        ObservableList<RepairIssue> selectedIssues = tableIssueList.getSelectionModel().getSelectedItems();
        if (selectedIssues == null || selectedIssues.isEmpty()) {
            showError("Vui lòng chọn ít nhất một lỗi để sửa.");
            return;
        }

        // Tổng chi phí
        double totalCost = selectedIssues.stream().mapToDouble(RepairIssue::getCost).sum();

        // Lấy tên lỗi
        List<String> issueNames = selectedIssues.stream()
                .map(RepairIssue::getName)
                .collect(Collectors.toList());

        // Tạo đối tượng RepairHistory
        RepairHistory newRepair = new RepairHistory(
                0,
                deviceId,
                technicianId,
                issueNames,
                repairDateTime,
                null,
                "Chưa hoàn thành",
                totalCost
        );
        newRepair.setTechnicianName(technicianName);
        newRepair.setDeviceName(deviceName);
        System.out.println(">>> Device Name Set: " + newRepair.getDeviceName());

        // Gọi service để lưu
        try {
            RepairHistoryServices service = new RepairHistoryServices();
            boolean success = service.addRepairHistory(newRepair, new ArrayList<>(selectedIssues));

            if (success) {
                showInfo("Thêm lịch sửa chữa thành công!");
                loadRepairHistoryData();
            } else {
                showError("Không thể thêm lịch sửa chữa.");
            }
        } catch (SQLException e) {
            showError("Lỗi khi lưu lịch sử sửa chữa: " + e.getMessage());
        }
    }

    private void addRepairHistoryIssues(Connection conn, int repairHistoryId, ObservableList<RepairIssue> issues) throws SQLException {
        String sql = "INSERT INTO repair_history_repair_issue (repair_history_id, repair_issue_id) VALUES (?, ?)";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            for (RepairIssue issue : issues) {
                stm.setInt(1, repairHistoryId);
                stm.setInt(2, issue.getId());
                stm.addBatch();
            }
            stm.executeBatch(); // Thực thi hàng loạt để tăng hiệu suất
        }
    }

    private void markAsCompleted(RepairHistory repair) {
        if (!"Hoàn thành".equals(repair.getStatus())) {
            try (Connection conn = JdbcUtils.getConn()) {
                String sql = "UPDATE repair_history SET status = ?, completion_date = ? WHERE id = ?";
                PreparedStatement stm = conn.prepareStatement(sql);

                LocalDateTime now = LocalDateTime.now(); // Lấy thời gian hiện tại
                stm.setString(1, "Hoàn thành");
                stm.setTimestamp(2, Timestamp.valueOf(now));
                stm.setInt(3, repair.getId());

                int rowsUpdated = stm.executeUpdate();
                if (rowsUpdated > 0) {
                    // Cập nhật lại dữ liệu trong đối tượng RepairHistory
                    repair.setStatus("Hoàn thành");
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
            List<RepairHistory> repairHistories = repairHistoryService.getRepairHistories();

            // Kiểm tra nếu repairHistories không phải null và không rỗng
            if (repairHistories != null && !repairHistories.isEmpty()) {
                tableRepairHistory.setItems(FXCollections.observableList(repairHistories));
            } else {
                showError("Không có dữ liệu lịch sửa chữa để hiển thị.");
            }
        } catch (SQLException e) {
            showError("Lỗi khi tải dữ liệu lịch sửa chữa: " + e.getMessage());
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
            String sql = "SELECT COUNT(*) FROM repair_history WHERE technician_id = ? AND status = 'Chưa hoàn thành'";
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
