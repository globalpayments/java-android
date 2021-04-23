package com.global.api;

import com.global.api.entities.exceptions.ApiException;
import com.global.api.entities.exceptions.ConfigurationException;
import com.global.api.serviceConfigs.Configuration;
import com.global.api.terminals.DeviceController;
import com.global.api.terminals.abstractions.IDeviceInterface;
import com.global.api.terminals.abstractions.IDisposable;

import java.util.HashMap;

public class ServicesContainer implements IDisposable {
    private HashMap<String, ConfiguredServices> configurations;
    private static ServicesContainer instance;

    public IDeviceInterface getDeviceInterface(String configName) throws ApiException {
        if (configurations.containsKey(configName))
            return configurations.get(configName).getDeviceInterface();
        throw new ApiException("The specified configuration has not been configured for terminal interaction.");
    }

    public DeviceController getDeviceController(String configName) throws ApiException {
        if (configurations.containsKey(configName))
            return configurations.get(configName).getDeviceController();
        throw new ApiException("The specified configuration has not been configured for terminal interaction.");
    }

    public static ServicesContainer getInstance() {
        if (instance == null)
            instance = new ServicesContainer();
        return instance;
    }

    public static void configure(ServicesConfig config) throws ConfigurationException {
        configure(config, "default");
    }

    public static void configure(ServicesConfig config, String configName) throws ConfigurationException {
        config.validate();

        // configure devices
        configureService(config.getDeviceConnectionConfig(), configName);

        ConfiguredServices cs = new ConfiguredServices();

        // configure devices
        if (config.getDeviceConnectionConfig() != null) {

        }
    }

    public static <T extends Configuration> void configureService(T config) throws ConfigurationException {
        configureService(config, "default");
    }

    public static <T extends Configuration> void configureService(T config, String configName) throws ConfigurationException {
        if (config == null)
            return;

        if (!config.isValidated())
            config.validate();

        ConfiguredServices cs = getInstance().getConfiguration(configName);
        config.configureContainer(cs);

        getInstance().addConfiguration(configName, cs);
    }

    private ServicesContainer() {
        configurations = new HashMap<String, ConfiguredServices>();
    }

    private ConfiguredServices getConfiguration(String configName) {
        if (configurations.containsKey(configName))
            return configurations.get(configName);
        return new ConfiguredServices();
    }

    private void addConfiguration(String configName, ConfiguredServices cs) {
        if (configurations.containsKey(configName))
            configurations.remove(configName);
        configurations.put(configName, cs);
    }

    public void dispose() {
        for (ConfiguredServices cs : configurations.values())
            cs.dispose();
    }
}
