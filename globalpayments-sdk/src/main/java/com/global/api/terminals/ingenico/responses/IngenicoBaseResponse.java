package com.global.api.terminals.ingenico.responses;

import com.global.api.terminals.DeviceResponse;
import com.global.api.terminals.TerminalUtilities;
import com.global.api.terminals.ingenico.variables.DynamicCurrencyStatus;
import com.global.api.terminals.ingenico.variables.ParseFormat;
import com.global.api.terminals.ingenico.variables.PaymentMode;
import com.global.api.terminals.ingenico.variables.TransactionStatus;
import com.global.api.terminals.ingenico.variables.TransactionSubTypes;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

public abstract class IngenicoBaseResponse extends DeviceResponse {

    protected byte[] _buffer;
    protected ParseFormat _format;
    protected DataResponse _respField;

    private String _dccCurrency;
    private DynamicCurrencyStatus _dccStatus;
    private BigDecimal _dccAmount;
    private TransactionSubTypes _transactionSubType;
    private BigDecimal _splitSaleAmount;
    private PaymentMode _paymentMode;
    private String _currencyCode;
    private String _privateData;
    private BigDecimal _finalTransactionAmount;
    private String _amount;

    public IngenicoBaseResponse(byte[] buffer, ParseFormat format) {
        if (buffer != null) {
            _buffer = buffer;
            _format = format;
            if (_format != ParseFormat.XML) {
                parseResponse(_buffer);
            }
        }
    }

    public void parseResponse(byte[] response) {
        if (response != null) {
            String strBuffer = TerminalUtilities.getString(response);

            setReferenceNumber(strBuffer.substring(0, 2));
            setStatus(TransactionStatus.getEnumName(Integer.parseInt(strBuffer.substring(2, 3))).toString());
            setAmount(strBuffer.substring(3, 11));
            setPaymentMode(PaymentMode.getEnumName(Integer.parseInt(strBuffer.substring(11, 12))));
            setCurrencyCode(strBuffer.substring(67, 70));
            setPrivateData(strBuffer.substring(70, strBuffer.length()));

            if (_format == ParseFormat.Transaction) {
                String respField = strBuffer.substring(12, 67);
                _respField = new DataResponse(respField.getBytes());

                setFinalTransactionAmount(_respField.getFinalAmount());
                setDccAmount(_respField.getDccAmount());
                setDccCurrency(_respField.getDccCode());
                setDccStatus(_respField.getDccStatus());
            }
        }
    }

    public String getDccCurrency() {
        return _dccCurrency;
    }

    public void setDccCurrency(String dccCurrency) {
        _dccCurrency = dccCurrency;
    }

    public DynamicCurrencyStatus getDccStatus() {
        return _dccStatus;
    }

    public void setDccStatus(DynamicCurrencyStatus dccStatus) {
        _dccStatus = dccStatus;
    }

    public BigDecimal getDccAmount() {
        return _dccAmount;
    }

    public void setDccAmount(BigDecimal dccAmount) {
        _dccAmount = dccAmount;
    }

    public TransactionSubTypes getTransactionSubType() {
        return _transactionSubType;
    }

    public void setTransactionSubType(TransactionSubTypes transactionSubType) {
        _transactionSubType = transactionSubType;
    }

    public BigDecimal getSplitSaleAmount() {
        return _splitSaleAmount;
    }

    public void setSplitSaleAmount(BigDecimal splitSaleAmount) {
        _splitSaleAmount = splitSaleAmount;
    }

    public PaymentMode getPaymentMode() {
        return _paymentMode;
    }

    public void setPaymentMode(PaymentMode paymentMode) {
        _paymentMode = paymentMode;
    }

    public String getCurrencyCode() {
        return _currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        _currencyCode = currencyCode;
    }

    public String getPrivateData() {
        return _privateData;
    }

    public void setPrivateData(String privateData) {
        _privateData = privateData;
    }

    public BigDecimal getFinalTransactionAmount() {
        return _finalTransactionAmount;
    }

    public void setFinalTransactionAmount(BigDecimal finalTransactionAmount) {
        _finalTransactionAmount = finalTransactionAmount;
    }

    public String getAmount() {
        return _amount;
    }

    public void setAmount(String amount) {
        _amount = amount;
    }

    @Override
    public String toString() {
        String rawData = new String(_buffer, StandardCharsets.UTF_8);
        setDeviceResponseText(rawData);

        return getDeviceResponseText();
    }
}