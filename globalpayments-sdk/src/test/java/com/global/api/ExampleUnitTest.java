package com.global.api;

import com.global.api.entities.enums.ConnectionModes;
import com.global.api.entities.enums.DeviceType;
import com.global.api.services.DeviceService;
import com.global.api.terminals.ConnectionConfig;
import com.global.api.terminals.abstractions.IDeviceInterface;
import com.global.api.terminals.abstractions.ITerminalResponse;
import com.global.api.terminals.messaging.IBroadcastMessageInterface;
import com.global.api.terminals.messaging.IMessageReceivedInterface;
import com.global.api.terminals.messaging.IMessageSentInterface;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    IDeviceInterface _device;

    public ExampleUnitTest() {
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
    public void SaleTest() {
        try {
            ITerminalResponse response = _device.sale(new BigDecimal("6.18"))
                    .withReferenceNumber(1)
                    .withCashBack(new BigDecimal(3))
                    .withCurrencyCode("826")
                    .execute();

            assertNotNull(response);
            assertNotNull(response.getAuthorizationCode());

            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void RefundTest() {
        try {
            ITerminalResponse response = _device.refund(new BigDecimal("6.18"))
                    .withReferenceNumber(1)
                    .withCashBack(new BigDecimal(3))
                    .withCurrencyCode("826")
                    .execute();

            assertNotNull(response);
            assertNotNull(response.getAuthorizationCode());

            Thread.sleep(2000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void PreAuthTest() {
        try {
            ITerminalResponse response = _device.authorize(new BigDecimal("6.18"))
                    .withReferenceNumber(1)
                    .withCashBack(new BigDecimal(3))
                    .withCurrencyCode("826")
                    .execute();

            assertNotNull(response);
            assertNotNull(response.getAuthorizationCode());

            Thread.sleep(2000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void CompletionTest() {
        try {
            ITerminalResponse response = _device.capture(new BigDecimal("6.18"))
                    .withTransactionId("124")
                    .withReferenceNumber(1)
                    .withCurrencyCode("826")
                    .execute();

            assertNotNull(response);
            assertNotNull(response.getAuthorizationCode());

            Thread.sleep(2000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void AccountVerificationTest() {
        try {
            ITerminalResponse response = _device.verify()
                    .withReferenceNumber(1)
                    .withCashBack(new BigDecimal(3))
                    .withCurrencyCode("826")
                    .execute();

            assertNotNull(response);
            assertNotNull(response.getAuthorizationCode());

            Thread.sleep(2000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void CancelTest() {
        try {
            ITerminalResponse response = _device.cancel(new BigDecimal("6.18"))
                    .withReferenceNumber(1)
                    .withTransactionId("1")
                    .execute();

            assertNotNull(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void DuplicateTest() {
        try {
            ITerminalResponse response = _device.duplicate(new BigDecimal("6.18"))
                    .withReferenceNumber(1)
                    .withTransactionId("1")
                    .execute();

            assertNotNull(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String[] broadcast = new String[10];
    String sent;
    String received;
    int i = 0;
    @Test
    public void ReverseTest() {
        try {
            _device.setOnBroadcastMessageReceived(new IBroadcastMessageInterface() {
                @Override
                public void broadcastReceived(String code, String message) {
                    i++;
                    broadcast[i] = code + " - " + message;
                }
            });

            _device.setOnMessageSent(new IMessageSentInterface() {
                @Override
                public void messageSent(String message) {
                    sent = message;
                }
            });

            _device.setOnMessageReceived(new IMessageReceivedInterface() {
                @Override
                public void messageReceived(String message) {
                    received = message;
                }
            });

            ITerminalResponse response = _device.reverse(new BigDecimal("6.18"))
                    .withReferenceNumber(1)
                    .withTransactionId("8153")
                    .execute();

            assertNotNull(response);
            assertNotNull(broadcast);
            assertNotNull(sent);
            assertNotNull(received);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}