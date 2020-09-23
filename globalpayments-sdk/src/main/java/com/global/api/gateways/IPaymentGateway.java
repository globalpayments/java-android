package com.global.api.gateways;

import android.view.SurfaceControl;

import com.global.api.builders.ReportBuilder;
import com.global.api.entities.exceptions.ApiException;

public interface IPaymentGateway {
//    SurfaceControl.Transaction processAuthorization(AuthorizationBuilder builder) throws ApiException;
//    SurfaceControl.Transaction manageTransaction(ManagementBuilder builder) throws ApiException;
    <T> T processReport(ReportBuilder<T> builder, Class<T> clazz) throws ApiException;
//    String serializeRequest(AuthorizationBuilder builder) throws ApiException;
    boolean supportsHostedPayments();
}
