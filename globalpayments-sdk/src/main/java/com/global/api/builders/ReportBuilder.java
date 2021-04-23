package com.global.api.builders;

import com.global.api.entities.enums.ReportType;
import com.global.api.entities.enums.TimeZoneConversion;

public abstract class ReportBuilder<TResult> extends BaseBuilder<TResult> {
    private ReportType reportType;
    private TimeZoneConversion timeZoneConversion;
    private Class<TResult> clazz;

    public ReportType getReportType() {
        return reportType;
    }
    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }
    public TimeZoneConversion getTimeZoneConversion() {
        return timeZoneConversion;
    }
    public void setTimeZoneConversion(TimeZoneConversion timeZoneConversion) {
        this.timeZoneConversion = timeZoneConversion;
    }

    public ReportBuilder(ReportType type, Class<TResult> clazz) {
        super();
        this.reportType = type;
        this.clazz = clazz;
    }

//    public TResult execute(String configName) throws ApiException {
//        super.execute(configName);
//
//        IPaymentGateway client = ServicesContainer.getInstance().getGateway(configName);
//        return client.processReport(this, clazz);
//    }
}
