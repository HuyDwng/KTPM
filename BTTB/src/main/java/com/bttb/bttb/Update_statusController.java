package com.bttb.bttb;

import com.bttb.pojo.Device;
import com.bttb.service.DeviceService;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.collections.FXCollections;
import java.util.List;

public class Update_statusController {

    @FXML
    private ComboBox<Device> cbDevice; // Chọn thiết bị

    @FXML
    private ComboBox<String> cbStatus; // Chọn trạng thái

    @FXML
    private Button btnConfirm;

    private DeviceService deviceService;
    private Device_managementController deviceManagementController;

    public void setDeviceService(DeviceService service) {
        this.deviceService = service;
        loadDeviceList();
    }

    public void setDeviceManagementController(Device_managementController controller) {
        this.deviceManagementController = controller;
    }

    

    private void loadDeviceList() {
        if (deviceService != null) {
            List<Device> devices = deviceService.getAllDevices();
            cbDevice.setItems(FXCollections.observableArrayList(devices));
        }
    }

    @FXML
    private void updateStatus() {
        Device selectedDevice = cbDevice.getValue();
        String selectedStatus = cbStatus.getValue();

        System.out.print(selectedDevice + "||");
        System.out.print(selectedDevice.getStatus() + "||");
        System.out.print(selectedDevice.getName() + "||");
        System.out.print(selectedDevice.getId() + "||");
        System.out.print("Đã thanh lý".equals(selectedDevice.getStatus()) + "||");
        System.out.print("Đã thanh lý");
        if (selectedDevice == null || selectedStatus == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn thiết bị và trạng thái!");
            return;
        }

        if ("Đã thanh lý".equals(selectedDevice.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Thiết bị này đã thanh lý và không thể cập nhật trạng thái!");
            return;
        }

        if (selectedStatus.equals(selectedDevice.getStatus())) {
            showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Trạng thái thiết bị không thay đổi.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION,
                "Thiết bị: " + selectedDevice.getName() + "\nTrạng thái mới: " + selectedStatus, ButtonType.OK, ButtonType.CANCEL);
        confirmDialog.setTitle("Xác nhận");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK && deviceService.updateDeviceStatus(selectedDevice.getId(), selectedStatus)) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật trạng thái thành công!");
                if (deviceManagementController != null) {
                    deviceManagementController.loadDeviceData();
                }
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật trạng thái!");
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        ((Stage) btnConfirm.getScene().getWindow()).close();
    }
    
    @FXML
    public void initialize() {
        cbStatus.setItems(FXCollections.observableArrayList("Đang hoạt động", "Đang sửa", "Hỏng hóc", "Đã thanh lý"));
        cbDevice.setOnAction(event -> {
            if (cbDevice.getValue() != null) {
                cbStatus.setValue(cbDevice.getValue().getStatus());
            }
        });
    }
}
