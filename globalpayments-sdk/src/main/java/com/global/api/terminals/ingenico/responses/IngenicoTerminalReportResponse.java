package com.global.api.terminals.ingenico.responses;

import androidx.annotation.NonNull;

import com.global.api.terminals.TerminalUtilities;
import com.global.api.terminals.abstractions.ITerminalReport;

public class IngenicoTerminalReportResponse extends com.global.api.terminals.ingenico.responses.IngenicoBaseResponse implements ITerminalReport {
    private byte[] buffer;
    private String _reportData;

    public IngenicoTerminalReportResponse(byte[] buffer) {
        this.buffer = buffer;
        _reportData = this.buffer.toString();
        String status = this.buffer.length > 0 ? "SUCCESS" : "FAILED";
        setStatus(status);
    }

    public String getReportData() {
        return this._reportData;
    }

    public void setReportData(String reportData) {
        this._reportData = reportData;
    }

    @NonNull
    @Override
    public String toString() {
        return TerminalUtilities.getString(buffer);
    }
}
