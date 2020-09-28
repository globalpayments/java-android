package com.global.api.terminals.ingenico.responses;

import com.global.api.terminals.abstractions.IDeviceResponse;
import com.global.api.terminals.ingenico.variables.LogOnStatus;
import com.global.api.terminals.ingenico.variables.ParseFormat;

import java.nio.charset.StandardCharsets;

public class LogOnResponse extends IngenicoTerminalResponse implements IDeviceResponse {
	private byte[] _buffer;

	public LogOnResponse(byte[] buffer) {
		super(buffer, ParseFormat.Transaction);
		_buffer = buffer;
		parseResponse(buffer);
	}

	@Override
	public void parseResponse(byte[] response) {
		super.parseResponse(response);
		String rawData = new String(response, StandardCharsets.UTF_8);
        setStatus(LogOnStatus.getEnumName(Integer.parseInt(rawData.substring(2, 3))).toString());
	}
	
	@Override
	public String toString() {
		return new String(_buffer, StandardCharsets.UTF_8);
	}
}
