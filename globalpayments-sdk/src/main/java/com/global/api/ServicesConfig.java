package com.global.api;

import com.global.api.entities.exceptions.ConfigurationException;
import com.global.api.terminals.ConnectionConfig;

public class ServicesConfig {
    private ConnectionConfig deviceConnectionConfig;

    public ConnectionConfig getDeviceConnectionConfig() {
        return deviceConnectionConfig;
    }

    public void setDeviceConnectionConfig(ConnectionConfig deviceConnectionConfig) {
        this.deviceConnectionConfig = deviceConnectionConfig;
    }

    public void setTimeout(int timeout) {
        if (deviceConnectionConfig != null)
            deviceConnectionConfig.setTimeout(timeout);
    }

    protected void validate() throws ConfigurationException {
        if (deviceConnectionConfig != null)
            deviceConnectionConfig.validate();
    }
}
