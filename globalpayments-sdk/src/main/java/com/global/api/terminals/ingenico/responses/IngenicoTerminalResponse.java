package com.global.api.terminals.ingenico.responses;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import com.global.api.entities.enums.ApplicationCryptogramType;
import com.global.api.terminals.abstractions.IDeviceResponse;
import com.global.api.terminals.abstractions.ITerminalResponse;
import com.global.api.terminals.ingenico.variables.PaymentMethod;
import com.global.api.terminals.ingenico.variables.TransactionStatus;
import com.global.api.terminals.ingenico.variables.TransactionSubTypes;
import com.global.api.utils.Extensions;
import com.global.api.terminals.ingenico.variables.DynamicCurrencyStatus;
import com.global.api.terminals.ingenico.variables.ParseFormat;
import com.global.api.terminals.ingenico.variables.PaymentMode;

public class IngenicoTerminalResponse extends IngenicoBaseResponse implements ITerminalResponse, IDeviceResponse {
    private String responseText;
    private String responseCode;
    private String transactionId;
    private String token;
    private String signatureStatus;
    private byte[] signatureData;
    private String transactionType;
    private String maskedCardNumber;
    private String entryMethod;
    private String approvalCode;
    private BigDecimal amountDue;
    private String cardHolderName;
    private String cardBIN;
    private boolean cardPresent;
    private String expirationDate;
    private String avsResponseCode;
    private String avsResponseText;
    private String cvvResponseCode;
    private String cvvResponseText;
    private boolean taxExempt;
    private String taxExemptId;
    private String ticketNumber;
    private ApplicationCryptogramType applicationCryptogramType;
    private String applicationCryptogram;
    private String cardHolderVerificationMethod;
    private String terminalVerificationResults;
    private String applicationPreferredName;
    private String applicationLabel;
    private String applicationId;

    public IngenicoTerminalResponse(byte[] buffer, ParseFormat format) {
        super(buffer, format);
    }

    public BigDecimal getTransactionAmount() {
        BigDecimal transactionAmount = new BigDecimal(getAmount())
                .multiply(new BigDecimal("100"));

        return transactionAmount;
    }

    public BigDecimal getBalanceAmount() {
        return _respField.getAvailableAmount();
    }

    public void setBalanceAmount(BigDecimal balanceAmount) {
        _respField.setAvailableAmount(balanceAmount);
    }

    public String getAuthorizationCode() {
        return _respField.getAuthorizationCode();
    }

    public void setAuthorizationCode(String authorizationCode) {
        _respField.setAuthorizationCode(authorizationCode);
    }

    public BigDecimal getCashBackAmount() {
        return _respField.getCashbackAmount();
    }

    public void setCashBackAmount(BigDecimal cashBackAmount) {
        _respField.setCashbackAmount(cashBackAmount);
    }

    public BigDecimal getTipAmount() {
        return _respField.getGratuityAmount();
    }

    public void setTipAmount(BigDecimal tipAmount) {
        _respField.setGratuityAmount(tipAmount);
    }

    public String getPaymentType() {
        Integer paymentType =  _respField.getPaymentMethod() == null ? 0 : _respField.getPaymentMethod().getValue();
        return paymentType == 0 ? "" : PaymentMethod.getEnumName(paymentType).toString();
    }

    public void setPaymentType(String paymentType) {
        PaymentMethod paymentMethod = PaymentMethod.getEnumName(Integer.parseInt(paymentType));
        _respField.setPaymentMethod(paymentMethod);
    }

    public String getTerminalRefNumber() {
        return getReferenceNumber();
    }

    public void setTerminalRefNumber(String terminalRefNumber) {
        setReferenceNumber(terminalRefNumber);
    }

    // -------

    public void setTransactionAmount(BigDecimal transactionAmount) {
        setAmount(transactionAmount.toString());
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSignatureStatus() {
        return signatureStatus;
    }

    public void setSignatureStatus(String signatureStatus) {
        this.signatureStatus = signatureStatus;
    }

    public byte[] getSignatureData() {
        return signatureData;
    }

    public void setSignatureData(byte[] signatureData) {
        this.signatureData = signatureData;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public void setMaskedCardNumber(String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }

    public String getEntryMethod() {
        return entryMethod;
    }

    public void setEntryMethod(String entryMethod) {
        this.entryMethod = entryMethod;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public BigDecimal getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(BigDecimal amountDue) {
        this.amountDue = amountDue;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardBIN() {
        return cardBIN;
    }

    public void setCardBIN(String cardBIN) {
        this.cardBIN = cardBIN;
    }

    public boolean getCardPresent() {
        return cardPresent;
    }

    public void setCardPresent(boolean cardPresent) {
        this.cardPresent = cardPresent;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getAvsResponseCode() {
        return avsResponseCode;
    }

    public void setAvsResponseCode(String avsResponseCode) {
        this.avsResponseCode = avsResponseCode;
    }

    public String getAvsResponseText() {
        return avsResponseText;
    }

    public void setAvsResponseText(String avsResponseText) {
        this.avsResponseText = avsResponseText;
    }

    public String getCvvResponseCode() {
        return cvvResponseCode;
    }

    public void setCvvResponseCode(String cvvResponseCode) {
        this.cvvResponseCode = cvvResponseCode;
    }

    public String getCvvResponseText() {
        return cvvResponseText;
    }

    public void setCvvResponseText(String cvvResponseText) {
        this.cvvResponseText = cvvResponseText;
    }

    public boolean getTaxExempt() {
        return taxExempt;
    }

    public void setTaxExempt(boolean taxExempt) {
        this.taxExempt = taxExempt;
    }

    public String getTaxExemptId() {
        return taxExemptId;
    }

    public void setTaxExemptId(String taxExemptId) {
        this.taxExemptId = taxExemptId;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public ApplicationCryptogramType getApplicationCryptogramType() {
        return applicationCryptogramType;
    }

    public void setApplicationCryptogramType(ApplicationCryptogramType applicationCryptogramType) {
        this.applicationCryptogramType = applicationCryptogramType;
    }

    public String getApplicationCryptogram() {
        return applicationCryptogram;
    }

    public void setApplicationCryptogram(String applicationCryptogram) {
        this.applicationCryptogram = applicationCryptogram;
    }

    public String getCardHolderVerificationMethod() {
        return cardHolderVerificationMethod;
    }

    public void setCardHolderVerificationMethod(String cardHolderVerificationMethod) {
        this.cardHolderVerificationMethod = cardHolderVerificationMethod;
    }

    public String getTerminalVerificationResults() {
        return terminalVerificationResults;
    }

    public void setTerminalVerificationResults(String terminalVerificationResults) {
        this.terminalVerificationResults = terminalVerificationResults;
    }

    public String getApplicationPreferredName() {
        return applicationPreferredName;
    }

    public void setApplicationPreferredName(String applicationPreferredName) {
        this.applicationPreferredName = applicationPreferredName;
    }

    public String getApplicationLabel() {
        return applicationLabel;
    }

    public void setApplicationLabel(String applicationLabel) {
        this.applicationLabel = applicationLabel;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}
