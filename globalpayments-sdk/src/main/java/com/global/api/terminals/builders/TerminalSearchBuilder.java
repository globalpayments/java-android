package com.global.api.terminals.builders;

import java.lang.reflect.Field;

import com.global.api.entities.exceptions.ApiException;
import com.global.api.terminals.abstractions.ITerminalReport;

public class TerminalSearchBuilder {
    private TerminalReportBuilder _reportBuilder;

    private int recordNumber;
    private int terminalReferenceNumber;
    private String authCode;
    private String referenceNumber;
    private int merchantId;
    private String merchantName;

    public int getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(int recordNumber) {
        this.recordNumber = recordNumber;
    }

    public int getTerminalReferenceNumber() {
        return terminalReferenceNumber;
    }

    public void setTerminalReferenceNumber(int terminalReferenceNumber) {
        this.terminalReferenceNumber = terminalReferenceNumber;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public TerminalSearchBuilder(TerminalReportBuilder reportBuilder) {
        _reportBuilder = reportBuilder;
    }

    public ITerminalReport execute() throws ApiException {
        return execute("default");
    }

    public ITerminalReport execute(String configName) throws ApiException {
        return _reportBuilder.execute(configName);
    }

    private <T> void set(Object object, String fieldName, T fieldValue) {
        Class<?> clazz = object.getClass();

        // https://stackoverflow.com/questions/4052840/most-efficient-way-to-make-the-first-character-of-a-string-lower-case
        char c[] = fieldName.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        fieldName = new String(c);

        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}