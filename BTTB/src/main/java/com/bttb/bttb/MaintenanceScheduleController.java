package com.bttb.bttb;

import com.bttb.pojo.Device;
import com.bttb.pojo.EmailUtils;
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
import javafx.util.StringConverter;

public class MaintenanceScheduleController implements Initializable {

    @FXML
    private ComboBox<Device> comboBoxDevices;
    @FXML
    private ComboBox<String> comboBoxFrequency;
    @FXML
    private ComboBox<String> comboBoxExecutor;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField txtTime;
    @FXML
    private Button btnSchedule;
    @FXML
    private Label lblMessage;

    private final ScheduleServices ss = new ScheduleServices();
    private ObservableList<Device> activeDevices;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        lblMessage.setVisible(false);
        setupDatePicker();
        setupTimeField();
        loadExecutors();

        comboBoxFrequency.setItems(FXCollections.observableArrayList(
                "Hàng tuần", "Hàng tháng", "Hàng năm"
        ));
        comboBoxFrequency.getSelectionModel().selectFirst();

        try {
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

    private void setupComboBoxSearch() {
        comboBoxDevices.setEditable(true);

        FilteredList<Device> filteredList = new FilteredList<>(activeDevices, p -> true);
        comboBoxDevices.setItems(filteredList);

        comboBoxDevices.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            filteredList.setPredicate(device -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                return device.getName().toLowerCase().contains(newValue.toLowerCase())
                        || String.valueOf(device.getId()).contains(newValue);
            });

            // Nếu đang gõ, chưa chọn item → clear selection
            if (comboBoxDevices.getSelectionModel().getSelectedItem() == null
                    || !comboBoxDevices.getSelectionModel().getSelectedItem().getName().equalsIgnoreCase(newValue)) {
                comboBoxDevices.getSelectionModel().clearSelection();
            }
        });

        comboBoxDevices.setConverter(new StringConverter<Device>() {
            @Override
            public String toString(Device device) {
                return device != null ? String.format("ID: %d - %s", device.getId(), device.getName()) : "";
            }

            @Override
            public Device fromString(String string) {
                return activeDevices.stream()
                        .filter(d -> String.format("ID: %d - %s", d.getId(), d.getName()).equalsIgnoreCase(string)
                        || d.getName().equalsIgnoreCase(string))
                        .findFirst().orElse(null);
            }
        });

        comboBoxDevices.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Device item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("ID: %d - %s", item.getId(), item.getName()));
            }
        });

        comboBoxDevices.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Device item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("ID: %d - %s", item.getId(), item.getName()));
            }
        });

        // Khi focus rời khỏi editor → ép chọn đúng device nếu có
        comboBoxDevices.getEditor().focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                String input = comboBoxDevices.getEditor().getText();
                Device matched = activeDevices.stream()
                        .filter(d -> String.format("ID: %d - %s", d.getId(), d.getName()).equalsIgnoreCase(input)
                        || d.getName().equalsIgnoreCase(input))
                        .findFirst().orElse(null);

                comboBoxDevices.getSelectionModel().select(matched);
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

    public void loadExecutors() {
        ObservableList<String> executors = ScheduleServices.loadExecutors();
        comboBoxExecutor.setItems(executors);
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

//        if (scheduleService.addMaintenanceSchedule(...)) {
//            showSuccess("Lập lịch thành công!");
//
//            // Gửi email
//            String executorEmail = ScheduleServices.getEmailByExecutorName(selectedExecutor);
//            if (executorEmail != null) {
//                EmailUtils.sendEmail(executorEmail,
//                        "Thông báo lịch bảo trì",
//                        String.format("Bạn được phân công bảo trì thiết bị '%s' vào lúc %s %s.",
//                                selectedDevice.getName(),
//                                selectedDate.toString(),
//                                selectedTime.toString())
//                );
//            }
//
//        } else {
//            showError("Lưu lịch bảo trì thất bại!");
//        }

        try {
            LocalDate selectedDate = datePicker.getValue();
            LocalTime selectedTime = LocalTime.parse(txtTime.getText());
            String selectedFrequency = comboBoxFrequency.getSelectionModel().getSelectedItem();
            String selectedExecutor = comboBoxExecutor.getSelectionModel().getSelectedItem();

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

            if (scheduleService.addMaintenanceSchedule(selectedDevice.getId(), selectedDate, selectedTime, selectedFrequency, selectedExecutor)) {
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
        if (comboBoxFrequency.getSelectionModel().getSelectedItem() == null) {
            showError("Vui lòng chọn tần suất!");
            return false;
        }
        if (comboBoxExecutor.getSelectionModel().getSelectedItem() == null) {
            showError("Vui lòng chọn kỹ thuật viên!");
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
