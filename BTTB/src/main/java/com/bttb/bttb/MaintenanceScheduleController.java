package com.bttb.bttb;

import com.bttb.pojo.Device;
import com.bttb.pojo.EmailUtils;
import com.bttb.pojo.User;
import com.bttb.services.ScheduleServices;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    private ComboBox<User> comboBoxExecutor;
    @FXML
    private DatePicker datePicker;
    @FXML
    private DatePicker deadlinePicker;
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
        setupDeadlinePicker();
        setupTimeField();
        loadExecutors();

        comboBoxFrequency.setItems(FXCollections.observableArrayList(
                "Hàng tuần", "Hàng tháng"
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return string != null && !string.isEmpty() ? LocalDate.parse(string, formatter) : null;
                } catch (Exception e) {
                    return null;
                }
            }
        });

        datePicker.setPromptText("dd/MM/yyyy");

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

    private void setupDeadlinePicker() {
        // Format hiển thị: dd/MM/yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        deadlinePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return string != null && !string.isEmpty() ? LocalDate.parse(string, formatter) : null;
                } catch (Exception e) {
                    return null;
                }
            }
        });

        deadlinePicker.setPromptText("dd/MM/yyyy");

        // Giới hạn không cho chọn ngày trước ngày bắt đầu
        deadlinePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                LocalDate startDate = datePicker.getValue();
                if (item != null && startDate != null && item.isBefore(startDate)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #EEEEEE;");
                }
            }
        });

        // Đặt ngày mặc định là +3 tháng từ ngày bắt đầu
        if (datePicker.getValue() != null) {
            deadlinePicker.setValue(datePicker.getValue().plusMonths(3));
        }

        // Tự cập nhật khi ngày bắt đầu thay đổi
        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                deadlinePicker.setValue(newDate.plusMonths(3));
                setupDeadlinePicker(); // Gọi lại để update DayCellFactory
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

        txtTime.setOnAction(event -> formatTimeInput());
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
        ObservableList<User> executors = ss.loadExecutors();
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

        LocalDate selectedDate = datePicker.getValue();
        LocalDate nextMaintenanceDate = deadlinePicker.getValue();
        LocalTime selectedTime = LocalTime.parse(txtTime.getText());
        String selectedFrequency = comboBoxFrequency.getSelectionModel().getSelectedItem();
        User selectedUser = (User) comboBoxExecutor.getValue();
        int executorId = selectedUser.getId();
        String executorName = selectedUser.toString();

        try {
            if (selectedDate == null || selectedTime == null) {
                showError("Vui lòng nhập ngày và giờ hợp lệ!");
                return;
            }

            LocalDateTime scheduleDateTime = LocalDateTime.of(selectedDate, selectedTime);

            if (scheduleDateTime.isBefore(LocalDateTime.now())) {
                showError("Thời gian lập lịch phải ở tương lai!");
                return;
            }

            if (deadlinePicker.getValue() == null) {
                showError("Vui lòng chọn hạn bảo trì!");
                return;
            }
            if (deadlinePicker.getValue().isBefore(datePicker.getValue())) {
                showError("Hạn bảo trì phải sau ngày lập lịch!");
                return;
            }

            if (ss.isScheduleDuplicate(selectedDevice.getId(), scheduleDateTime)) {
                showError("Lịch bảo trì đã tồn tại vào thời gian này!");
                return;
            }

            if (ss.addMaintenanceSchedule(selectedDevice.getId(), selectedDate, selectedTime, selectedFrequency, executorId, nextMaintenanceDate)) {
                showSuccess("Lập lịch thành công!");

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                
                String toEmail = ScheduleServices.getExecutorEmail(executorId);

                String subject = "Thông báo lập lịch bảo trì thiết bị";
                String content = String.format(
                        "Thiết bị '%s' đã được lập lịch bảo trì vào lúc %s %s, với tần suất: %s.\n"
                        + "Người thực hiện: %s.\nHạn bảo trì tiếp theo: %s.",
                        selectedDevice.getName(),
                        selectedDate.toString(),
                        selectedTime.toString(),
                        selectedFrequency,
                        executorName,
                        nextMaintenanceDate.format(dateFormatter)
                );

                boolean emailSent = EmailUtils.sendEmail(toEmail, subject, content);
                if (!emailSent) {
                    showError("Lập lịch thành công nhưng gửi email thất bại!");
                }

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
