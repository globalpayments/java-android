package com.global.api.gateways;

import com.global.api.builders.ReportBuilder;
import com.global.api.entities.exceptions.ApiException;

public interface IPaymentGateway {
    <T> T processReport(ReportBuilder<T> builder, Class<T> clazz) throws ApiException;

    boolean supportsHostedPayments();
}
