package com.global.api.terminals.ingenico.responses;

import com.global.api.terminals.abstractions.IDeviceResponse;
import com.global.api.terminals.ingenico.variables.CancelStatus;

import java.nio.charset.StandardCharsets;

public class CancelResponse extends IngenicoTerminalResponse implements IDeviceResponse {

    public CancelResponse(byte[] buffer) {
        super(buffer);
        ParseResponse(buffer);
    }

    @Override
    public void ParseResponse(byte[] response) {
        super.ParseResponse(response);
        String _response = new String(response, StandardCharsets.UTF_8);
        setStatus(CancelStatus.getEnumName(Integer.parseInt(_response.substring(2, 3))).toString());
    }
}
