package com.global.api.builders;

import com.global.api.entities.enums.TransactionType;
import com.global.api.paymentMethods.IPaymentMethod;

public abstract class TransactionBuilder<TResult> extends BaseBuilder<TResult> {
    protected boolean forceGatewayTimeout;
    protected TransactionType transactionType;
    protected IPaymentMethod paymentMethod;

    // network fields
    protected int batchNumber;
    protected String companyId;
    protected int sequenceNumber;
    protected int systemTraceAuditNumber;
    protected String uniqueDeviceId;

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public IPaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(IPaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isForceGatewayTimeout() {
        return forceGatewayTimeout;
    }

    // network fields
    public int getBatchNumber() { return batchNumber; }

    public String getCompanyId() {
        return companyId;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getSystemTraceAuditNumber() {
        return systemTraceAuditNumber;
    }

    public String getUniqueDeviceId() {
        return uniqueDeviceId;
    }

    public TransactionBuilder(TransactionType type) {
        this(type, null);
    }
    public TransactionBuilder(TransactionType type, IPaymentMethod paymentMethod){
        super();
        this.transactionType = type;
        this.paymentMethod = paymentMethod;
    }
}
