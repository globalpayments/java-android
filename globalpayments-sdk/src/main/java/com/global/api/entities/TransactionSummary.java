package com.global.api.entities;

import android.location.Address;

import java.math.BigDecimal;

import com.global.api.entities.reporting.AltPaymentData;
import com.global.api.entities.reporting.CheckData;

import org.joda.time.DateTime;

public class TransactionSummary {
    private String accountDataSource;
    private AltPaymentData altPaymentData;
    private BigDecimal amount;
    private BigDecimal amountDue;
    private BigDecimal authorizedAmount;
    private String authCode;
    private String avsResponseCode;
    private DateTime batchCloseDate;
    private String batchId;
    private String batchSequenceNumber;
    private Address billingAddress;
    private BigDecimal captureAmount;
    private String cardEntryMethod;
    private String cardHolderFirstName;
    private String cardHolderLastName;
    private String cardSwiped;
    private String cardType;
    private BigDecimal cashBackAmount;
    private String cavvResponseCode;
    private CheckData checkData;
    private String clerkId;
    private String clientTransactionId;
    private String companyName;
    private BigDecimal convenienceAmount;
    private String customerFirstName;
    private String customerId;
    private String customerLastName;
    private String cvnResponseCode;
    private boolean debtRepaymentIndicator;
    private String description;
    private int deviceId;
    private String eciIndicator;
    private String emvChipCondition;
    private String fraudRuleInfo;
    private boolean fullyCaptured;
    private BigDecimal gratuityAmount;
    private boolean hasEcomPaymentData;
    private boolean hasEmvTags;
    private Boolean hostTimeOut;
    private String invoiceNumber;
    private String issuerResponseCode;
    private String issuerResponseMessage;
    private String issuerTransactionId;
    private String gatewayResponseCode;
    private String gatewayResponseMessage;
    private String giftCurrency;
    private LodgingData lodgingData;
    private String maskedAlias;
    private String maskedCardNumber;
    private boolean oneTimePayment;
    private String originalTransactionId;
    private String paymentMethodKey;
    private String paymentType;
    private String poNumber;
    private String recurringDataCode;
    private String referenceNumber;
    private int repeatCount;
    private DateTime responseDate;
    private String scheduleId;
    private String serviceName;
    private BigDecimal settlementAmount;
    private BigDecimal shippingAmount;
    private String siteTrace;
    private String status;
    private BigDecimal surchargeAmount;
    private BigDecimal taxAmount;
    private String taxType;
    private String tokenPanLastFour;
    private DateTime transactionDate;
    private String transactionDescriptor;
    private String transactionId;
    private String transactionStatus;
    private String transactionType;
    private String uniqueDeviceId;
    private String username;
    private String xid;

    public String getAccountDataSource() {
        return accountDataSource;
    }

    public void setAccountDataSource(String accountDataSource) {
        this.accountDataSource = accountDataSource;
    }

    public AltPaymentData getAltPaymentData() {
        return altPaymentData;
    }

    public void setAltPaymentData(AltPaymentData altPaymentData) {
        this.altPaymentData = altPaymentData;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(BigDecimal amountDue) {
        this.amountDue = amountDue;
    }

    public BigDecimal getAuthorizedAmount() {
        return authorizedAmount;
    }

    public void setAuthorizedAmount(BigDecimal authorizedAmount) {
        this.authorizedAmount = authorizedAmount;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getAvsResponseCode() {
        return avsResponseCode;
    }

    public void setAvsResponseCode(String avsResponseCode) {
        this.avsResponseCode = avsResponseCode;
    }

    public DateTime getBatchCloseDate() {
        return batchCloseDate;
    }

    public void setBatchCloseDate(DateTime batchCloseDate) {
        this.batchCloseDate = batchCloseDate;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getBatchSequenceNumber() {
        return batchSequenceNumber;
    }

    public void setBatchSequenceNumber(String batchSequenceNumber) {
        this.batchSequenceNumber = batchSequenceNumber;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public BigDecimal getCaptureAmount() {
        return captureAmount;
    }

    public void setCaptureAmount(BigDecimal captureAmount) {
        this.captureAmount = captureAmount;
    }

    public String getCardEntryMethod() {
        return cardEntryMethod;
    }

    public void setCardEntryMethod(String cardEntryMethod) {
        this.cardEntryMethod = cardEntryMethod;
    }

    public String getCardHolderFirstName() {
        return cardHolderFirstName;
    }

    public void setCardHolderFirstName(String cardHolderFirstName) {
        this.cardHolderFirstName = cardHolderFirstName;
    }

    public String getCardHolderLastName() {
        return cardHolderLastName;
    }

    public void setCardHolderLastName(String cardHolderLastName) {
        this.cardHolderLastName = cardHolderLastName;
    }

    public String getCardSwiped() {
        return cardSwiped;
    }

    public void setCardSwiped(String cardSwiped) {
        this.cardSwiped = cardSwiped;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public BigDecimal getCashBackAmount() {
        return cashBackAmount;
    }

    public void setCashBackAmount(BigDecimal cashBackAmount) {
        this.cashBackAmount = cashBackAmount;
    }

    public String getCavvResponseCode() {
        return cavvResponseCode;
    }

    public void setCavvResponseCode(String cavvResponseCode) {
        this.cavvResponseCode = cavvResponseCode;
    }

    public CheckData getCheckData() {
        return checkData;
    }

    public void setCheckData(CheckData checkData) {
        this.checkData = checkData;
    }

    public String getClerkId() {
        return clerkId;
    }

    public void setClerkId(String clerkId) {
        this.clerkId = clerkId;
    }

    public String getClientTransactionId() {
        return clientTransactionId;
    }

    public void setClientTransactionId(String clientTransactionId) {
        this.clientTransactionId = clientTransactionId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public BigDecimal getConvenienceAmount() {
        return convenienceAmount;
    }

    public void setConvenienceAmount(BigDecimal convenienceAmount) {
        this.convenienceAmount = convenienceAmount;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getCvnResponseCode() {
        return cvnResponseCode;
    }

    public void setCvnResponseCode(String cvnResponseCode) {
        this.cvnResponseCode = cvnResponseCode;
    }

    public boolean isDebtRepaymentIndicator() {
        return debtRepaymentIndicator;
    }

    public void setDebtRepaymentIndicator(boolean debtRepaymentIndicator) {
        this.debtRepaymentIndicator = debtRepaymentIndicator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getEciIndicator() {
        return eciIndicator;
    }

    public void setEciIndicator(String eciIndicator) {
        this.eciIndicator = eciIndicator;
    }

    public String getEmvChipCondition() {
        return emvChipCondition;
    }

    public void setEmvChipCondition(String emvChipCondition) {
        this.emvChipCondition = emvChipCondition;
    }

    public String getFraudRuleInfo() {
        return fraudRuleInfo;
    }

    public void setFraudRuleInfo(String fraudRuleInfo) {
        this.fraudRuleInfo = fraudRuleInfo;
    }

    public boolean isFullyCaptured() {
        return fullyCaptured;
    }

    public void setFullyCaptured(boolean fullyCaptured) {
        this.fullyCaptured = fullyCaptured;
    }

    public BigDecimal getGratuityAmount() {
        return gratuityAmount;
    }

    public void setGratuityAmount(BigDecimal gratuityAmount) {
        this.gratuityAmount = gratuityAmount;
    }

    public boolean isHasEcomPaymentData() {
        return hasEcomPaymentData;
    }

    public void setHasEcomPaymentData(boolean hasEcomPaymentData) {
        this.hasEcomPaymentData = hasEcomPaymentData;
    }

    public boolean isHasEmvTags() {
        return hasEmvTags;
    }

    public void setHasEmvTags(boolean hasEmvTags) {
        this.hasEmvTags = hasEmvTags;
    }

    public Boolean getHostTimeOut() {
        return hostTimeOut;
    }

    public void setHostTimeOut(Boolean hostTimeOut) {
        this.hostTimeOut = hostTimeOut;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getIssuerResponseCode() {
        return issuerResponseCode;
    }

    public void setIssuerResponseCode(String issuerResponseCode) {
        this.issuerResponseCode = issuerResponseCode;
    }

    public String getIssuerResponseMessage() {
        return issuerResponseMessage;
    }

    public void setIssuerResponseMessage(String issuerResponseMessage) {
        this.issuerResponseMessage = issuerResponseMessage;
    }

    public String getIssuerTransactionId() {
        return issuerTransactionId;
    }

    public void setIssuerTransactionId(String issuerTransactionId) {
        this.issuerTransactionId = issuerTransactionId;
    }

    public String getGatewayResponseCode() {
        return gatewayResponseCode;
    }

    public void setGatewayResponseCode(String gatewayResponseCode) {
        this.gatewayResponseCode = gatewayResponseCode;
    }

    public String getGatewayResponseMessage() {
        return gatewayResponseMessage;
    }

    public void setGatewayResponseMessage(String gatewayResponseMessage) {
        this.gatewayResponseMessage = gatewayResponseMessage;
    }

    public String getGiftCurrency() {
        return giftCurrency;
    }

    public void setGiftCurrency(String giftCurrency) {
        this.giftCurrency = giftCurrency;
    }

    public LodgingData getLodgingData() {
        return lodgingData;
    }

    public void setLodgingData(LodgingData lodgingData) {
        this.lodgingData = lodgingData;
    }

    public String getMaskedAlias() {
        return maskedAlias;
    }

    public void setMaskedAlias(String maskedAlias) {
        this.maskedAlias = maskedAlias;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public void setMaskedCardNumber(String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }

    public boolean isOneTimePayment() {
        return oneTimePayment;
    }

    public void setOneTimePayment(boolean oneTimePayment) {
        this.oneTimePayment = oneTimePayment;
    }

    public String getOrderId() {
        return this.clientTransactionId;
    }

    public void setOrderId(String value) {
        this.clientTransactionId = value;
    }

    public String getOriginalTransactionId() {
        return originalTransactionId;
    }

    public void setOriginalTransactionId(String originalTransactionId) {
        this.originalTransactionId = originalTransactionId;
    }

    public String getPaymentMethodKey() {
        return paymentMethodKey;
    }

    public void setPaymentMethodKey(String paymentMethodKey) {
        this.paymentMethodKey = paymentMethodKey;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getRecurringDataCode() {
        return recurringDataCode;
    }

    public void setRecurringDataCode(String recurringDataCode) {
        this.recurringDataCode = recurringDataCode;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public DateTime getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(DateTime responseDate) {
        this.responseDate = responseDate;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public BigDecimal getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(BigDecimal settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public BigDecimal getShippingAmount() {
        return shippingAmount;
    }

    public void setShippingAmount(BigDecimal shippingAmount) {
        this.shippingAmount = shippingAmount;
    }

    public String getSiteTrace() {
        return siteTrace;
    }

    public void setSiteTrace(String siteTrace) {
        this.siteTrace = siteTrace;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getSurchargeAmount() {
        return surchargeAmount;
    }

    public void setSurchargeAmount(BigDecimal surchargeAmount) {
        this.surchargeAmount = surchargeAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getTokenPanLastFour() {
        return tokenPanLastFour;
    }

    public void setTokenPanLastFour(String tokenPanLastFour) {
        this.tokenPanLastFour = tokenPanLastFour;
    }

    public DateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(DateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionDescriptor() {
        return transactionDescriptor;
    }

    public void setTransactionDescriptor(String transactionDescriptor) {
        this.transactionDescriptor = transactionDescriptor;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getUniqueDeviceId() {
        return uniqueDeviceId;
    }

    public void setUniqueDeviceId(String uniqueDeviceId) {
        this.uniqueDeviceId = uniqueDeviceId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }
}
