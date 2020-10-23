package com.global.api;

import android.os.Handler;

import java.math.BigDecimal;

import org.junit.Test;

import com.global.api.entities.enums.ConnectionModes;
import com.global.api.entities.enums.DeviceType;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.services.DeviceService;
import com.global.api.terminals.ConnectionConfig;
import com.global.api.terminals.abstractions.IDeviceInterface;
import com.global.api.terminals.ingenico.pat.PATRequest;
import com.global.api.terminals.ingenico.variables.PATPaymentMode;
import com.global.api.terminals.ingenico.variables.PATRequestType;
import com.global.api.terminals.ingenico.variables.PATResponseType;
import com.global.api.terminals.messaging.IOnPayAtTableRequestInterface;

public class IngenicoPayAtTable {
	private IDeviceInterface device;
	ConnectionConfig config = new ConnectionConfig();

	public IngenicoPayAtTable() throws ApiException {
		config.setDeviceType(DeviceType.INGENICO);
		config.setConnectionMode(ConnectionModes.PAY_AT_TABLE);
		config.setPort("18101");
		config.setTimeout(0);
		device = DeviceService.create(config);
	}

	@Test
	public void payAtTableHandling() throws InterruptedException {
		device.setOnPayAtTableRequest(new IOnPayAtTableRequestInterface() {
			@Override
			public void onPayAtTableRequest(final PATRequest payAtTableRequest) {
				new Thread(new Runnable() {
					@Override
					public void run () {
						payAtTableResponse(payAtTableRequest);
					}
				}).start();
			}
		});

		Thread.sleep(3000 * 1000);
	}

	private void payAtTableResponse(PATRequest payAtTableRequest) {
		try {
			PATRequestType requestType = payAtTableRequest.getRequestType();
			if (requestType == PATRequestType.TABLE_LOCK) {
				device.payAtTableResponse().withPATTResponseType(PATResponseType.CONF_OK)
						.withPATTPaymentMode(PATPaymentMode.NO_ADDITIONAL_MSG).withAmount(new BigDecimal(6.18))
						.withCurrencyCode("826")
						.execute();
			} else if (requestType == PATRequestType.RECEIPT_MESSAGE) {
				device.payAtTableResponse()
						.withXML("C:/Users/raniel.antonio/Desktop/XML/receiptrequestsample.xml")
						.execute();
			} else if (requestType == PATRequestType.TABLE_LIST) {
				device.payAtTableResponse()
						.withXML("C:/Users/raniel.antonio/Desktop/XML/tlist.xml")
						.execute();
			} else if (requestType == PATRequestType.TRANSACTION_OUTCOME) {
				device.payAtTableResponse().withPATTResponseType(PATResponseType.CONF_OK)
						.withPATTPaymentMode(PATPaymentMode.NO_ADDITIONAL_MSG).withAmount(new BigDecimal(6.18))
						.withCurrencyCode("826")
						.execute();
			} else if (requestType == PATRequestType.ADDITIONAL_MESSAGE) {
				device.payAtTableResponse()
						.withXML("C:/Users/raniel.antonio/Desktop/XML/addtl_message.xml")
						.execute();
			} else if (requestType == PATRequestType.SPLITSALE_REPORT) {
				device.payAtTableResponse().withPATTResponseType(PATResponseType.CONF_OK)
						.withPATTPaymentMode(PATPaymentMode.NO_ADDITIONAL_MSG).withAmount(new BigDecimal(6.18))
						.withCurrencyCode("826")
						.execute();
			} else if (requestType == PATRequestType.TICKET) {
				device.payAtTableResponse().withPATTResponseType(PATResponseType.CONF_OK)
						.withPATTPaymentMode(PATPaymentMode.NO_ADDITIONAL_MSG).withAmount(new BigDecimal(6.18))
						.withCurrencyCode("826")
						.execute();
			} else if (requestType == PATRequestType.EOD_REPORT) {
				device.payAtTableResponse().withPATTResponseType(PATResponseType.CONF_OK)
						.withPATTPaymentMode(PATPaymentMode.NO_ADDITIONAL_MSG).withAmount(new BigDecimal(6.18))
						.withCurrencyCode("826")
						.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
