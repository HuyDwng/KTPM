package com.bttb.bttb;

import com.bttb.pojo.Device;
import com.bttb.services.DeviceServices;
import com.bttb.services.ScheduleServices;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class MaintenanceScheduleController implements Initializable {

    @FXML
    private TableView<Device> tableDevices;
    @FXML
    private TableColumn<Device, String> colName;
    @FXML
    private TableColumn<Device, String> colStatus;
    @FXML
    private ComboBox<Device> comboBoxDevices;
    @FXML
    private ComboBox<String> frequencyComboBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField txtTime;
    @FXML
    private Button btnSchedule;
    @FXML
    private Label lblMessage;

    private final ScheduleServices ss = new ScheduleServices();
    private final DeviceServices ds = new DeviceServices();
    private ObservableList<Device> activeDevices;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblMessage.setVisible(false);
        setupDatePicker();
        setupTimeField();

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        frequencyComboBox.setItems(FXCollections.observableArrayList(
            "Hàng ngày", "Hàng tuần", "Hàng tháng", "Hàng năm"
        ));
        frequencyComboBox.getSelectionModel().selectFirst();
        try {
            tableDevices.setItems(FXCollections.observableArrayList(ds.getDevices()));

            // Lưu danh sách thiết bị hoạt động làm biến toàn cục
            activeDevices = ss.getActiveDevices();
            comboBoxDevices.setItems(activeDevices);

            setupComboBoxSearch(); // Thiết lập tìm kiếm cho ComboBox

        } catch (SQLException e) {
            showError("Lỗi khi tải dữ liệu thiết bị: " + e.getMessage());
        }

        btnSchedule.setOnAction(event -> {
            try {
                handleScheduleButton();
            } catch (SQLException ex) {
                Logger.getLogger(MaintenanceScheduleController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Thêm tìm kiếm vào ComboBox
    private void setupComboBoxSearch() {
        comboBoxDevices.setEditable(true);

        // Tạo danh sách lọc từ danh sách thiết bị đang hoạt động
        FilteredList<Device> filteredList = new FilteredList<>(FXCollections.observableArrayList(activeDevices), p -> true);

        comboBoxDevices.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            filteredList.setPredicate(device -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                return device.getName().toLowerCase().contains(newValue.toLowerCase());
            });

            // Cập nhật danh sách thiết bị cho ComboBox
            comboBoxDevices.setItems(filteredList);
        });

        // Đảm bảo hiển thị đúng thông tin của đối tượng Device trong ComboBox
        comboBoxDevices.setConverter(new StringConverter<Device>() {
            @Override
            public String toString(Device device) {
                return device != null ? device.getName() : "";
            }

            @Override
            public Device fromString(String string) {
                return activeDevices.stream()
                        .filter(device -> device.getName().equalsIgnoreCase(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    private void setupDatePicker() {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #EEEEEE;");
                }
            }
        });

        datePicker.setValue(LocalDate.now());
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

    @FXML
    private void handleScheduleButton() throws SQLException {
        if (!validateInputs()) {
            return;
        }

        Device selectedDevice = comboBoxDevices.getSelectionModel().getSelectedItem();
        if (!"Đang hoạt động".equals(selectedDevice.getStatus())) {
            showError("Chỉ có thể lập lịch cho thiết bị đang hoạt động!");
            return;
        }

        try {
            LocalDate selectedDate = datePicker.getValue();
            LocalTime selectedTime = LocalTime.parse(txtTime.getText());

            if (selectedDate == null || selectedTime == null) {
                showError("Vui lòng nhập ngày và giờ hợp lệ!");
                return;
            }

            LocalDateTime scheduleDateTime = LocalDateTime.of(selectedDate, selectedTime);
            if (scheduleDateTime.isBefore(LocalDateTime.now())) {
                showError("Thời gian lập lịch phải ở tương lai!");
                return;
            }

            ScheduleServices scheduleService = new ScheduleServices();
            if (scheduleService.isScheduleDuplicate(selectedDevice.getId(), scheduleDateTime)) {
                showError("Lịch bảo trì đã tồn tại vào thời gian này!");
                return;
            }

            if (scheduleService.addMaintenanceSchedule(selectedDevice.getId(), selectedDate, selectedTime)) {
                showSuccess("Lập lịch thành công!");
            } else {
                showError("Lưu lịch bảo trì thất bại!");
            }
        } catch (SQLException e) {
            showError("Lỗi SQL: " + e.getMessage());
        } catch (Exception e) {
            showError("Lỗi nhập liệu: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (comboBoxDevices.getSelectionModel().getSelectedItem() == null) {
            showError("Vui lòng chọn thiết bị!");
            return false;
        }
        if (txtTime.getText().isEmpty()) {
            showError("Vui lòng nhập thời gian!");
            return false;
        }
        if (datePicker.getValue() == null) {
            showError("Vui lòng chọn ngày!");
            return false;
        }
        if (!txtTime.getText().matches("([01]?\\d|2[0-3]):[0-5]\\d")) {
            showError("Thời gian không đúng định dạng (HH:mm)!");
            return false;
        }
        if (datePicker.getValue().isBefore(java.time.LocalDate.now())) {
            showError("Ngày lập lịch phải là tương lai!");
            return false;
        }
        return true;
    }

    private void showSuccess(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        lblMessage.setVisible(true);
    }

    private void showError(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        lblMessage.setVisible(true);
    }
}
