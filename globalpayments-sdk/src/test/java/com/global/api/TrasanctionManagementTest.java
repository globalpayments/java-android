package com.global.api;

import com.global.api.entities.enums.ConnectionModes;
import com.global.api.entities.enums.DeviceType;
import com.global.api.services.DeviceService;
import com.global.api.terminals.ConnectionConfig;
import com.global.api.terminals.abstractions.IDeviceInterface;
import com.global.api.terminals.abstractions.ITerminalResponse;
import com.global.api.terminals.ingenico.variables.ReceiptType;
import com.global.api.terminals.ingenico.variables.ReportTypes;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class TrasanctionManagementTest {
    IDeviceInterface _device;

    public TrasanctionManagementTest() {
        try {
            ConnectionConfig config = new ConnectionConfig();
            config.setDeviceType(DeviceType.INGENICO);
            config.setConnectionMode(ConnectionModes.TCP_IP_SERVER);
            config.setPort("18101");
            config.setTimeout(30 * 1000);
            _device = DeviceService.create(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(_device);
    }

    @Test
    public void EndOfDayTest() {
        try {
            ITerminalResponse response = _device.getReport(ReportTypes.EOD)
                    .execute();

            assertNotNull(response);
        } catch (Exception e) {
            String errMsg = e.getMessage();
            e.printStackTrace();
        }
    }

    @Test
    public void BankingTest() {
        try {
            ITerminalResponse response = _device.getReport(ReportTypes.BANKING)
                    .execute();

            assertNotNull(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void XBalanceTest() {
        try {
            ITerminalResponse response = _device.getReport(ReportTypes.XBAL)
                    .execute();

            assertNotNull(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ZBalanceTest() {
        try {
            ITerminalResponse response = _device.getReport(ReportTypes.ZBAL)
                    .execute();

            assertNotNull(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
