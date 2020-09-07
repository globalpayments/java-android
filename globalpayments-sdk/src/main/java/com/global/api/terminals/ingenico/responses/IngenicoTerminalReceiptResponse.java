package com.global.api.terminals.ingenico.responses;

import java.nio.charset.StandardCharsets;

import com.global.api.terminals.abstractions.ITerminalReport;

public class IngenicoTerminalReceiptResponse extends IngenicoBaseResponse implements ITerminalReport {
    private byte[] buffer;

    public IngenicoTerminalReceiptResponse(byte[] buffer) {
        this.buffer = buffer;
        rawData = new String(this.buffer, StandardCharsets.ISO_8859_1);
        String status = this.buffer.length > 0 ? "SUCCESS" : "FAILED";
        setStatus(status);
    }
}
