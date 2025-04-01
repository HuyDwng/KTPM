package com.bttb.bttb;

import com.bttb.pojo.Device;
import com.bttb.services.DeviceServices;
import com.bttb.services.ScheduleServices;
import com.bttb.services.Utils;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private DatePicker datePicker;
    @FXML
    private TextField txtTime;
    @FXML
    private Button btnSchedule;
    @FXML
    private Label lblMessage;

    private final DeviceServices deviceService = new DeviceServices();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ScheduleServices service = new ScheduleServices();

        setupDatePicker();
        setupTimeField();
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 🔹 Load danh sách thiết bị vào bảng
        try {
            tableDevices.setItems(service.getDevices());
            comboBoxDevices.setItems(service.getActiveDevices());
        } catch (SQLException e) {
            showError("Lỗi khi tải dữ liệu thiết bị: " + e.getMessage());
        }
        btnSchedule.setOnAction(event -> handleScheduleButton());
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        datePicker.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String text) {
                try {
                    return (text != null && !text.isEmpty()) ? LocalDate.parse(text, formatter) : null;
                } catch (Exception e) {
                    return null;
                }
            }
        });

        datePicker.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            try {
                LocalDate parsedDate = LocalDate.parse(newText, formatter);
                datePicker.setValue(parsedDate);
            } catch (Exception ignored) {
            }
        });

        datePicker.setValue(LocalDate.now());
    }

    public LocalTime getSelectedTime() {
        return LocalTime.parse(txtTime.getText());
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
                    txtTime.positionCaret(3); // Tự động nhảy qua dấu `:`
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
    private void handleScheduleButton() {
        // Kiểm tra các input có hợp lệ không
        if (!validateInputs()) {
            return;
        }

        // Lấy thiết bị được chọn
        Device selectedDevice = comboBoxDevices.getSelectionModel().getSelectedItem();
        if (selectedDevice == null) {
            showError("Vui lòng chọn thiết bị!");
            return;
        }

        // Kiểm tra trạng thái của thiết bị
        if (!"Đang hoạt động".equals(selectedDevice.getStatus())) {
            showError("Chỉ có thể lập lịch cho thiết bị đang hoạt động!");
            return;
        }

        // Lấy ngày và giờ từ DatePicker & TextField
        try {
            LocalDate selectedDate = datePicker.getValue();
            LocalTime selectedTime = LocalTime.parse(txtTime.getText());

            if (selectedDate == null || selectedTime == null) {
                showError("Vui lòng nhập ngày và giờ hợp lệ!");
                return;
            }

            // Chuyển thành LocalDateTime để khớp với `isScheduleDuplicate()`
            LocalDateTime scheduleDateTime = LocalDateTime.of(selectedDate, selectedTime);

            // Kiểm tra thời gian có phải tương lai không
            if (scheduleDateTime.isBefore(LocalDateTime.now())) {
                showError("Thời gian lập lịch phải ở tương lai!");
                return;
            }

            ScheduleServices scheduleService = new ScheduleServices();

            // Kiểm tra trùng lịch
            if (scheduleService.isScheduleDuplicate(selectedDevice.getId(), scheduleDateTime)) {
                showError("Lịch bảo trì đã tồn tại vào thời gian này!");
                return;
            }

            // Thêm lịch bảo trì vào database (đổi lại tham số cho đúng)
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
        if (comboBoxDevices.getSelectionModel().isEmpty()) {
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
    }

    private void showError(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    }

}
