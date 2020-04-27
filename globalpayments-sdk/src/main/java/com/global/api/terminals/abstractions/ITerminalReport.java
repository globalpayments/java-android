package com.global.api.terminals.abstractions;

public interface ITerminalReport extends IDeviceResponse {
    String getReportData();
    void setReportData(String reportData);
}
