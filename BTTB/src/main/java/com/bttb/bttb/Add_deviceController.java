package com.bttb.bttb;

import com.bttb.pojo.Device;
import com.bttb.service.DeviceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class Add_deviceController {

    @FXML
    private TextField txtName;
    @FXML
    private ComboBox<String> statusComboBox;
    
    private DeviceService deviceService;

    public void setDeviceManagementController(Device_managementController controller) {
        this.deviceService = new DeviceService(controller);
    }

    public void addDevice() {
        String name = txtName.getText();
        String status = statusComboBox.getValue();

        if (name.isEmpty() || status.isEmpty()) {
            showAlert("Vui lòng nhập đầy đủ thông tin!", Alert.AlertType.ERROR);
            return;
        }

        Device device = new Device(name, status);
        boolean success = deviceService.addDevice(device);

        if (success) {
            showAlert("Thêm thiết bị thành công!", Alert.AlertType.INFORMATION);

        } else {
            showAlert("Lỗi khi thêm thiết bị!", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    public void initialize() {
        // Danh sách trạng thái thiết bị
        ObservableList<String> statusList = FXCollections.observableArrayList("Đang hoạt động", "Hỏng hóc", "Đang sửa");
        statusComboBox.setItems(statusList);

        // Đặt giá trị mặc định
        statusComboBox.setValue("Đang Hoạt động");
    }
}
