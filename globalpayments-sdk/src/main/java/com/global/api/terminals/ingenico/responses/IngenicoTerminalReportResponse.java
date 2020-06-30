package com.global.api.terminals.ingenico.responses;

import com.global.api.terminals.abstractions.ITerminalReport;

import java.nio.charset.StandardCharsets;

public class IngenicoTerminalReportResponse extends IngenicoTerminalResponse implements ITerminalReport {

    public IngenicoTerminalReportResponse(byte[] buffer) {
        super(buffer);
        parseResponse(buffer);
    }

    @Override
    public void parseResponse(byte[] response) {
        super.parseResponse(response);
        rawData = new String(response, StandardCharsets.UTF_8);
        privateData = rawData.substring(70);
    }
}
