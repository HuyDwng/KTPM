package com.bttb.bttb;

import com.bttb.pojo.Device;
import com.bttb.pojo.EmailUtils;
import com.bttb.pojo.JdbcUtils;
import com.bttb.pojo.MaintenanceSchedule;
import com.bttb.pojo.User;
import com.bttb.services.ScheduleServices;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class MaintenanceScheduleController implements Initializable {

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab tabManagement;
//    Lập lịch
    @FXML
    private VBox rootVBox;
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

//    Quản lý lịch bảo trì
    @FXML
    private TableView<MaintenanceSchedule> scheduleTable;
    @FXML
    private TableColumn<MaintenanceSchedule, Integer> colId;
    @FXML
    private TableColumn<MaintenanceSchedule, String> colDevice;
    @FXML
    private TableColumn<MaintenanceSchedule, String> colExecutor;
    @FXML
    private TableColumn<MaintenanceSchedule, LocalDate> colScheduledDate;
    @FXML
    private TableColumn<MaintenanceSchedule, LocalTime> colScheduledTime;
    @FXML
    private TableColumn<MaintenanceSchedule, String> colFrequency;
    @FXML
    private TableColumn<MaintenanceSchedule, LocalDate> colDeadlineDate;
    @FXML
    private TableColumn<MaintenanceSchedule, LocalDate> colCreatedAt;
    @FXML
    private TableColumn<MaintenanceSchedule, Void> colAction;
    @FXML
    private TableColumn<MaintenanceSchedule, LocalDate> colLastMaintenanceDate;

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
            activeDevices = ss.getActiveDevices();
            setupComboBoxDevices();

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

        Platform.runLater(() -> rootVBox.requestFocus());
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == tabManagement) {
                lblMessage.setVisible(false);
                addActionButtons();
                loadScheduleTableData();
            }
        });
    }

//    ---------------------LẬP LỊCH BẢO TRÌ-----------------------
    private void setupComboBoxDevices() {
        comboBoxDevices.setEditable(false);
        comboBoxDevices.setItems(activeDevices);

        comboBoxDevices.setConverter(new StringConverter<>() {
            @Override
            public String toString(Device device) {
                return (device == null) ? "" : String.format("ID: %d - %s", device.getId(), device.getName());
            }

            @Override
            public Device fromString(String string) {
                return null; // Không cần từ chuỗi -> Device vì không tìm kiếm nữa
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
        LocalDate selectedDate = datePicker.getValue();
        LocalDate maintenancePeriod = deadlinePicker.getValue();
        LocalTime selectedTime = LocalTime.parse(txtTime.getText());
        String selectedFrequency = comboBoxFrequency.getSelectionModel().getSelectedItem();
        User selectedUser = (User) comboBoxExecutor.getValue();
        int executorId = selectedUser.getId();
        String executorName = selectedUser.toString();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        if (ss.addMaintenanceSchedule(selectedDevice.getId(), selectedDate, selectedTime, selectedFrequency, executorId, maintenancePeriod)) {
            showSuccess("Lập lịch thành công!");

            // Tính thời gian còn lại đến ngày bảo trì - 24 giờ
            LocalDateTime maintenanceDateTime = LocalDateTime.of(selectedDate, selectedTime);
            LocalDateTime emailTime = maintenanceDateTime.minusHours(24);
            long delay = Duration.between(LocalDateTime.now(), emailTime).toMillis();

            // Nếu delay > 0 thì mới đặt lịch gửi mail
            if (delay > 0) {
                Runnable sendEmailTask = () -> {
                    String toEmail = ScheduleServices.getExecutorEmail(executorId);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String content = String.format(
                            "Thiết bị '%s' sẽ được bảo trì vào lúc %s %s, với tần suất: %s.\n"
                            + "Người thực hiện: %s.\nHạn bảo trì: %s.",
                            selectedDevice.getName(),
                            selectedDate.toString(),
                            selectedTime.toString(),
                            selectedFrequency,
                            executorName,
                            maintenancePeriod.format(formatter)
                    );

                    boolean emailSent = EmailUtils.sendEmail(toEmail, "Nhắc bảo trì thiết bị", content);
                    if (!emailSent) {
                        System.err.println("Gửi email nhắc thất bại!");
                    }
                };

                scheduler.schedule(sendEmailTask, delay, TimeUnit.MILLISECONDS);
            } else {
                System.out.println("Thời gian bảo trì quá gần, không thể đặt email trước 24h.");
            }

            //Load lại bảng
            loadScheduleTableData();
        } else {
            showError("Lưu lịch bảo trì thất bại!");
        }
    }

    private boolean validateInputs() {
        Device selectedDevice = comboBoxDevices.getSelectionModel().getSelectedItem();
        if (selectedDevice == null) {
            showError("Vui lòng chọn thiết bị!");
            return false;
        }

        if (!"Đang hoạt động".equals(selectedDevice.getStatus())) {
            showError("Chỉ có thể lập lịch cho thiết bị đang hoạt động!");
            return false;
        }

        String timeText = txtTime.getText();
        if (timeText.isEmpty()) {
            showError("Vui lòng nhập thời gian!");
            return false;
        }

        if (!timeText.matches("([01]?\\d|2[0-3]):[0-5]\\d")) {
            showError("Thời gian không đúng định dạng (HH:mm)!");
            return false;
        }

        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            showError("Vui lòng chọn ngày!");
            return false;
        }

        if (selectedDate.isBefore(LocalDate.now())) {
            showError("Ngày lập lịch phải là tương lai!");
            return false;
        }

        if (deadlinePicker.getValue() == null) {
            showError("Vui lòng chọn hạn bảo trì!");
            return false;
        }

        if (deadlinePicker.getValue().isBefore(selectedDate)) {
            showError("Hạn bảo trì phải sau ngày lập lịch!");
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

        try {
            if (ss.isScheduleDuplicate(selectedDevice.getId())) {
                showError("Thiết bị này đã có lịch bảo trì!");
                return false;
            }
        } catch (Exception e) {
            showError("Lỗi kiểm tra trùng lịch: " + e.getMessage());
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

//    ---------------------QUẢN LÝ LỊCH BẢO TRÌ-----------------------
    public void loadScheduleTableData() {
        try {
            ObservableList<MaintenanceSchedule> schedules = ss.getAllSchedules();
            colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
            colDevice.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDeviceName()));
            colExecutor.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getExecutorName()));
            colScheduledDate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getScheduledDate()));
            colScheduledTime.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getScheduledTime()));
            colFrequency.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getFrequency()));
            colDeadlineDate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getMaintenancePeriod()));
            colCreatedAt.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCreatedAt()));
            colLastMaintenanceDate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getLastMaintenanceDate()));

            scheduleTable.setItems(schedules);
            scheduleTable.refresh();
            colId.setSortType(TableColumn.SortType.ASCENDING);
            scheduleTable.getSortOrder().add(colId);
            scheduleTable.sort();
        } catch (SQLException e) {
            showError("Lỗi khi tải dữ liệu lịch bảo trì: " + e.getMessage());
        }
    }

    private void addActionButtons() {
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("Xóa");
            private final Button btnComplete = new Button("Hoàn thành");
            private final HBox box = new HBox(10, btnComplete, btnDelete);

            {
                btnDelete.setOnAction(evt -> {
                    MaintenanceSchedule ms = getTableView().getItems().get(getIndex());
                    handleDelete(ms);
                });

                btnComplete.setOnAction(evt -> {
                    MaintenanceSchedule ms = getTableView().getItems().get(getIndex());
                    handleComplete(ms);
                });
                btnDelete.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
                btnComplete.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                box.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    MaintenanceSchedule ms = getTableView().getItems().get(getIndex());
                    boolean isCompleted = ms.getLastMaintenanceDate() != null;
                    btnComplete.setDisable(isCompleted);
                    setGraphic(box);
                }
            }
        });
    }

    private void handleDelete(MaintenanceSchedule ms) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc muốn xóa lịch bảo trì này?");
        alert.setContentText("ID: " + ms.getId());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (ss.deleteSchedule(ms.getId())) {
                    showInfo("Đã xóa lịch bảo trì.");
                    loadScheduleTableData();
                } else {
                    showError("Không thể xóa lịch bảo trì.");
                }
            } catch (SQLException e) {
                showError("Lỗi khi xóa: " + e.getMessage());
            }
        }
    }

    private void handleComplete(MaintenanceSchedule ms) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận hoàn thành");
        alert.setHeaderText("Xác nhận đã bảo trì thiết bị?");
        alert.setContentText("Bạn có chắc chắn muốn đánh dấu lịch ID: " + ms.getId() + " là đã hoàn thành?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                LocalDate now = LocalDate.now();
                boolean success = ss.markAsCompleted(ms.getId(), now);

                if (success) {
                    showInfo("Đã đánh dấu hoàn thành.");
                    loadScheduleTableData();
                    scheduleTable.refresh();
                } else {
                    showError("Không thể cập nhật lịch bảo trì.");
                }
            } catch (SQLException ex) {
                showError("Lỗi SQL: " + ex.getMessage());
            }
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
  
    public static void showUpcomingMaintenance(List<MaintenanceSchedule> schedules, int daysAhead) {
        List<MaintenanceSchedule> upcoming = getUpcomingSchedules(schedules, daysAhead);

        if (upcoming.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo bảo trì");
            alert.setHeaderText("Không có lịch bảo trì sắp tới");
            alert.setContentText("Chưa có thiết bị nào cần bảo trì trong " + daysAhead + " ngày tới.");
            alert.show();
            return;
        }

        StringBuilder content = new StringBuilder();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (MaintenanceSchedule s : upcoming) {
            content.append("🔧 Thiết bị: ").append(s.getDeviceName())
                    .append("\n👨‍🔧 Người thực hiện: ").append(s.getExecutorName())
                    .append("\n📅 Ngày bảo trì: ").append(s.getLastMaintenanceDate())
                    .append(" lúc ").append(s.getScheduledTime())
                    .append("\n-------------------------------------\n");
        }

        TextArea textArea = new TextArea(content.toString());
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo bảo trì định kỳ");
        alert.setHeaderText("Có " + upcoming.size() + " thiết bị cần bảo trì trong " + daysAhead + " ngày tới");
        alert.getDialogPane().setContent(textArea);
        alert.show();
    }

    public static List<MaintenanceSchedule> getUpcomingSchedules(List<MaintenanceSchedule> schedules, int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusDays(daysAhead);

        return schedules.stream()
                .filter(s -> s.getScheduledDate() != null
                && !s.getScheduledDate().isBefore(today)
                && !s.getScheduledDate().isAfter(targetDate))
                .sorted(Comparator.comparing(MaintenanceSchedule::getScheduledDate))
                .collect(Collectors.toList());
    }
//    private void setupComboBoxSearch() {
//        if (activeDevices == null || activeDevices.isEmpty()) {
//            comboBoxDevices.getItems().clear();
//            return;
//        }
//
//        comboBoxDevices.setEditable(true);
//
//        // Dùng FilteredList để hỗ trợ tìm kiếm
//        FilteredList<Device> filteredList = new FilteredList<>(activeDevices, p -> true);
//        comboBoxDevices.setItems(filteredList);
//
//        // Tìm kiếm theo tên hoặc ID
//        comboBoxDevices.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
//            filteredList.setPredicate(device -> {
//                if (newVal == null || newVal.isEmpty()) {
//                    return true;
//                }
//                String lower = newVal.toLowerCase();
//                return device.getName().toLowerCase().contains(lower)
//                        || String.valueOf(device.getId()).contains(lower);
//            });
//
//            // Đảm bảo không thao tác select nếu list bị rỗng
//            if (!filteredList.isEmpty()) {
//                comboBoxDevices.show(); // đảm bảo show lại list sau filter
//            } else {
//                comboBoxDevices.hide(); // ẩn nếu rỗng để tránh bug UI
//            }
//        });
//
//        // Converter giữa object và chuỗi hiển thị
//        comboBoxDevices.setConverter(new StringConverter<>() {
//            @Override
//            public String toString(Device device) {
//                return (device == null) ? "" : String.format("ID: %d - %s", device.getId(), device.getName());
//            }
//
//            @Override
//            public Device fromString(String string) {
//                return activeDevices.stream()
//                        .filter(d -> String.format("ID: %d - %s", d.getId(), d.getName()).equalsIgnoreCase(string)
//                        || d.getName().equalsIgnoreCase(string))
//                        .findFirst().orElse(null);
//            }
//        });
//
//        // Cell hiển thị trong danh sách dropdown
//        comboBoxDevices.setCellFactory(cb -> new ListCell<>() {
//            @Override
//            protected void updateItem(Device item, boolean empty) {
//                super.updateItem(item, empty);
//                setText(empty || item == null ? null : String.format("ID: %d - %s", item.getId(), item.getName()));
//            }
//        });
//
//        // Cell hiển thị ở nút chính
//        comboBoxDevices.setButtonCell(new ListCell<>() {
//            @Override
//            protected void updateItem(Device item, boolean empty) {
//                super.updateItem(item, empty);
//                setText(empty || item == null ? null : String.format("ID: %d - %s", item.getId(), item.getName()));
//            }
//        });
//
//        // Show dropdown khi editor được focus
//        comboBoxDevices.getEditor().focusedProperty().addListener((obs, oldVal, newVal) -> {
//            if (newVal) {
//                comboBoxDevices.show();
//            } else {
//                String input = comboBoxDevices.getEditor().getText();
//                Device matched = filteredList.stream()
//                        .filter(d -> String.format("ID: %d - %s", d.getId(), d.getName()).equalsIgnoreCase(input)
//                        || d.getName().equalsIgnoreCase(input))
//                        .findFirst()
//                        .orElse(null);
//
//                if (matched != null) {
//                    // Chỉ select nếu filteredList có phần tử
//                    if (!filteredList.isEmpty()) {
//                        comboBoxDevices.getSelectionModel().select(matched);
//                    }
//                } else {
//                    comboBoxDevices.getSelectionModel().clearSelection();
//                }
//            }
//        });
//    }
}
