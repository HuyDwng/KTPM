package com.bttb.services;

import com.bttb.pojo.Device;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeviceServicesTest {

    @Test
    void testAddDevice() {
        DeviceServices deviceServices = mock(DeviceServices.class);
        Device device = new Device("PC Lab 1", "Đang hoạt động", 1);
        when(deviceServices.addDevice(any(Device.class))).thenReturn(true);

        boolean result = deviceServices.addDevice(device);
        assertTrue(result);
    }

    @Test
    void testDeleteDevice() {
        DeviceServices deviceServices = mock(DeviceServices.class);
        when(deviceServices.deleteDevice(10)).thenReturn(true);

        boolean result = deviceServices.deleteDevice(10);
        assertTrue(result);
    }

    @Test
    void testUpdateDeviceStatus() {
        DeviceServices deviceServices = mock(DeviceServices.class);
        when(deviceServices.updateDeviceStatus(5, "Hỏng hóc")).thenReturn(true);

        boolean result = deviceServices.updateDeviceStatus(5, "Hỏng hóc");
        assertTrue(result);
    }

    @Test
    void testAddDeviceWithEmptyName() {
        DeviceServices deviceServices = mock(DeviceServices.class);
        Device device = new Device("", "Đang hoạt động", 1);
        when(deviceServices.addDevice(device)).thenReturn(false);
        boolean result = deviceServices.addDevice(device);
        assertFalse(result);
    }

    @Test
    void testDeleteDeviceWithNegativeId() {
        DeviceServices deviceServices = mock(DeviceServices.class);
        when(deviceServices.deleteDevice(-1)).thenReturn(false);
        boolean result = deviceServices.deleteDevice(-1);
        assertFalse(result);
    }

    @Test
    void testUpdateDeviceStatusWithInvalidId() {
        DeviceServices deviceServices = mock(DeviceServices.class);
        when(deviceServices.updateDeviceStatus(-5, "Đang hoạt động")).thenReturn(false);
        boolean result = deviceServices.updateDeviceStatus(-5, "Đang hoạt động");
        assertFalse(result);
    }

    @Test
    void testAddDeviceWithDuplicateName() {
        DeviceServices deviceServices = mock(DeviceServices.class);
        Device device = new Device("PC Lab 1", "Đang hoạt động", 1);
        when(deviceServices.addDevice(device)).thenReturn(false); // Giả lập trùng tên
        boolean result = deviceServices.addDevice(device);
        assertFalse(result);
    }

    @Test
    void testUpdateDeviceStatusWithNullStatus() {
        DeviceServices deviceServices = mock(DeviceServices.class);
        when(deviceServices.updateDeviceStatus(1, null)).thenReturn(false);
        boolean result = deviceServices.updateDeviceStatus(1, null);
        assertFalse(result);
    }

    @Test
    void testUpdateDeviceStatusForLiquidatedDevice() {
        DeviceServices deviceServices = mock(DeviceServices.class);
        
        Device device = new Device(20, "Máy in Canon", "Đã thanh lý");
        
        when(deviceServices.updateDeviceStatus(eq(device.getId()), anyString())).thenReturn(false);
        boolean result = deviceServices.updateDeviceStatus(device.getId(), "Đang hoạt động");
        assertFalse(result);
    }
}
