package com.bttb.controller;

import com.bttb.pojo.Device;
import com.bttb.services.DeviceServices;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.collections.FXCollections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Update_statusController {

    @FXML
    private ComboBox<Device> cbDevice; // Chọn thiết bị

    @FXML
    private ComboBox<String> cbStatus; // Chọn trạng thái

    @FXML
    private Button btnConfirm;

    private DeviceServices deviceService;
    private Device_managementController deviceManagementController;

    public void setDeviceService(DeviceServices service) throws SQLException {
        this.deviceService = service;
        loadDeviceList();
    }

    public void setDeviceManagementController(Device_managementController controller) {
        this.deviceManagementController = controller;
    }

    private void loadDeviceList() throws SQLException {
        if (deviceService != null) {
            List<Device> devices = deviceService.getDevices();
            cbDevice.setItems(FXCollections.observableArrayList(devices));
        }
    }

    @FXML
    private void updateStatus() {
        Device selectedDevice = cbDevice.getValue();
        String selectedStatus = cbStatus.getValue();

        
        if (selectedDevice == null || selectedStatus == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn thiết bị và trạng thái!");
            return;
        }

        if ("Đã Thanh Lý".equals(selectedDevice.getStatus())) {
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
                    try {
                        deviceManagementController.loadDeviceData();
                    } catch (SQLException ex) {
                        Logger.getLogger(Update_statusController.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
        cbStatus.setItems(FXCollections.observableArrayList("Đang hoạt động", "Đang sửa", "Hỏng hóc", "Đã Thanh Lý"));
        cbDevice.setOnAction(event -> {
            if (cbDevice.getValue() != null) {
                cbStatus.setValue(cbDevice.getValue().getStatus());
            }
        });
    }
}
