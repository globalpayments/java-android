package com.global.api;

import com.global.api.entities.exceptions.ConfigurationException;
import com.global.api.terminals.DeviceController;
import com.global.api.terminals.abstractions.IDeviceInterface;
import com.global.api.terminals.abstractions.IDisposable;

public class ConfiguredServices implements IDisposable {
    private IDeviceInterface deviceInterface;
    private DeviceController deviceController;

    IDeviceInterface getDeviceInterface() {
        return deviceInterface;
    }
    DeviceController getDeviceController() {
        return deviceController;
    }
    public void setDeviceController(DeviceController deviceController) throws ConfigurationException {
        this.deviceController = deviceController;
        deviceInterface = deviceController.configureInterface();
    }

    public void dispose() {
        deviceController.dispose();
    }
}
