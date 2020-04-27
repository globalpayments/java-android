package com.global.api;

import com.global.api.entities.enums.ConnectionModes;
import com.global.api.entities.enums.DeviceType;
import com.global.api.services.DeviceService;
import com.global.api.terminals.ConnectionConfig;
import com.global.api.terminals.TerminalUtilities;
import com.global.api.terminals.abstractions.IDeviceInterface;
import com.global.api.terminals.abstractions.ITerminalReport;
import com.global.api.terminals.abstractions.ITerminalResponse;
import com.global.api.terminals.ingenico.variables.ReceiptType;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class XmlManagement {
    IDeviceInterface _device;

    public XmlManagement() {
        try {
            ConnectionConfig config = new ConnectionConfig();
            config.setDeviceType(DeviceType.INGENICO);
            config.setConnectionMode(ConnectionModes.TCP_IP_SERVER);
            config.setPort("18101");
            config.setTimeout(30000);
            _device = DeviceService.create(config);
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(_device);
    }

    @Test
    public void ReportTest() {
        try {
            ITerminalReport response = _device.getLastReceipt(ReceiptType.REPORT)
                    .Execute();

            assertNotNull(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TicketTest() {
        try {
            ITerminalReport response = _device.getLastReceipt(ReceiptType.TICKET)
                    .Execute();
            String resp = response.toString();
            assertNotNull(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
