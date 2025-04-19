/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.bttb.controller;

import com.bttb.pojo.Device;
import com.bttb.services.DeviceServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author nhanh
 */
public class DeviceManagementController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    public TableView<Device> deviceTable;
    @FXML
    private TableColumn<Device, Integer> colDeviceId;
    @FXML
    private TableColumn<Device, String> colDeviceName;
    @FXML
    private TableColumn<Device, String> colStatus;
    @FXML
    private TableColumn<Device, String> colType;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtDescription;
    @FXML
    private TextField txtStatus;

    private ObservableList<Device> deviceList = FXCollections.observableArrayList();

    private DeviceServices deviceService = new DeviceServices();

    public void openAddDeviceWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bttb/bttb/add_device.fxml"));
            Parent root = loader.load();

            AddDeviceController addDeviceController = loader.getController();
            addDeviceController.setDeviceManagementController(this);

            Stage stage = new Stage();
            stage.setTitle("Thêm Thiết Bị");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Chặn cửa sổ chính khi mở cửa sổ mới
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDevice() {
        String name = txtName.getText();
        String status = txtStatus.getText();

        if (name.isEmpty() || status.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        Device device = new Device(name, status);
        boolean success = deviceService.addDevice(device);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thêm thiết bị thành công!");

        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi thêm thiết bị!");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupTable() {
        colDeviceId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDeviceName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colType.setCellValueFactory(new PropertyValueFactory<>("deviceTypeName"));
    }

//    public void loadDeviceData() {
//        deviceList.clear();
//
//        String query = "SELECT d.id, d.name, d.status, dt.name AS device_type_name " +
//               "FROM device d JOIN device_type dt ON d.device_type_id = dt.id";
//
//        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
//
//            while (rs.next()) {
//                int id = rs.getInt("id");
//                String name = rs.getString("name");
//                String status = rs.getString("status");
//                int type = rs.getInt("device_type_id");
//
//                deviceList.add(new Device(id, name, status, type));
//            }
//
//            deviceTable.setItems(deviceList);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
    public void loadDeviceData() throws SQLException {
        deviceList.clear();
        deviceList.addAll(deviceService.getAllDevices());
        deviceTable.setItems(deviceList);
    }

//    @FXML
//    public void openAddDeviceForm() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("add_device.fxml"));
//            Parent root = loader.load();
//
//            // Lấy controller của Add_deviceController
//            Add_deviceController addDeviceController = loader.getController();
//            addDeviceController.setDeviceManagementController(this); // Truyền tham chiếu
//
//            Stage stage = new Stage();
//            stage.setScene(new Scene(root));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    @FXML
    private void openUpdateStatusForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("update_status.fxml"));
            Parent root = loader.load();

            UpdateStatusController controller = loader.getController();
            controller.setDeviceService(new DeviceServices());
            controller.setDeviceManagementController(this);

            Stage stage = new Stage();
            stage.setTitle("Cập nhật trạng thái");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteDevice() {
        Device selectedDevice = deviceTable.getSelectionModel().getSelectedItem();
        if (selectedDevice == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn thiết bị để xóa!");
            return;
        }

        // Xác nhận trước khi xóa thiết bị
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc chắn muốn xóa thiết bị: " + selectedDevice.getName() + "?",
                ButtonType.OK, ButtonType.CANCEL);
        confirmDialog.setTitle("Xác nhận");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = deviceService.deleteDevice(selectedDevice.getId());
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa thiết bị thành công!");
                    try {
                        loadDeviceData(); // Tải lại dữ liệu thiết bị
                    } catch (SQLException ex) {
                        Logger.getLogger(DeviceManagementController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa thiết bị!");
                }
            }
        });
    }

    @FXML
    private TextField searchField;  // Trường tìm kiếm

    public ObservableList<Device> allDeviceList = FXCollections.observableArrayList();
    public ObservableList<Device> filteredDeviceList = FXCollections.observableArrayList();

// Phương thức xử lý thay đổi trường tìm kiếm
    @FXML
    private void onSearchChanged() {
        String searchText = searchField.getText().toLowerCase();
        String selectedStatus = statusFilter.getValue();

        // Lọc thiết bị dựa trên tên và trạng thái
        filterDevices(searchText, selectedStatus);
    }

// Phương thức lọc thiết bị
    public void filterDevices(String searchText, String selectedStatus) {
        filteredDeviceList.clear();

        for (Device device : allDeviceList) {
            boolean matchesName = device.getName().toLowerCase().contains(searchText.toLowerCase());
            boolean matchesStatus = selectedStatus.equals("Tất cả") || device.getStatus().equals(selectedStatus);

            if (matchesName && matchesStatus) {
                filteredDeviceList.add(device);
            }
        }

        deviceTable.setItems(filteredDeviceList);  // Cập nhật bảng với danh sách đã lọc
    }

    @FXML
    private void loadDeviceDataSearch() {
        allDeviceList.clear();
        allDeviceList.addAll(deviceService.getAllDevicesForSearch());
        filterDevices(searchField.getText().toLowerCase(), statusFilter.getValue());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> statusOptions = FXCollections.observableArrayList(
                "Tất cả", "Đang hoạt động", "Hỏng hóc", "Đang sửa", "Đã thanh lý"
        );
        statusFilter.setItems(statusOptions);

        statusFilter.setValue("Tất cả");

        deviceTable.setRowFactory(tv -> {
            TableRow<Device> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    openUpdateStatusForm();
                }
            });
            return row;
        });

        setupTable();
        loadDeviceDataSearch();
        try {
            loadDeviceData();
        } catch (SQLException ex) {
            Logger.getLogger(DeviceManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
