package com.global.api.terminals.builders;

import com.global.api.ServicesContainer;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.terminals.DeviceController;
import com.global.api.terminals.TerminalReportType;
import com.global.api.terminals.abstractions.ITerminalReport;
import com.global.api.terminals.ingenico.variables.ReceiptType;

public class TerminalReportBuilder {
    private TerminalReportType reportType;
    private ReceiptType receiptType;
    private TerminalSearchBuilder _searchBuilder;

    public TerminalReportType getReportType() {
        return reportType;
    }

    public void setReportType(TerminalReportType reportType) {
        this.reportType = reportType;
    }

    public ReceiptType getReceiptType() {
        return receiptType;
    }

    public void setReceiptType(ReceiptType receiptType) {
        this.receiptType = receiptType;
    }

    public TerminalSearchBuilder getSearchBuilder() {
        if (_searchBuilder == null) {
            _searchBuilder = new TerminalSearchBuilder(this);
        }
        return _searchBuilder;

    }

    public TerminalReportBuilder(TerminalReportType reportType) {
        this.reportType = reportType;
    }

    public TerminalReportBuilder(ReceiptType receiptType) {
        this.receiptType = receiptType;
    }

//    public <T> TerminalSearchBuilder Where(PaxSearchCriteria criteria, T value) {
//        return getSearchBuilder().And(criteria, value);
//    }

    public ITerminalReport Execute() throws ApiException {
        return Execute("default");
    }

    public ITerminalReport Execute(String configName) throws ApiException {
        DeviceController device = ServicesContainer.getInstance().getDeviceController(configName);
        return device.processReport(this);
    }
}
