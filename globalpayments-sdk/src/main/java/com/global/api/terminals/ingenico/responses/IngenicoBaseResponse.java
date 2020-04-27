package com.global.api.terminals.ingenico.responses;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.math.BigDecimal;
import java.util.*;
import java.math.*;

import com.global.api.terminals.ingenico.*;
import com.global.api.entities.*;
import com.global.api.terminals.abstractions.*;

import com.global.api.terminals.*;
import com.global.api.utils.*;
import com.global.api.entities.enums.*;

public abstract class IngenicoBaseResponse implements IDeviceResponse {
    public String status;
    public String command;
    public String version;
    public String deviceResponseCode;
    public String deviceResponseText;
    public String referenceNumber;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDeviceResponseCode() {
        return deviceResponseCode;
    }

    public void setDeviceResponseCode(String deviceResponseCode) {
        this.deviceResponseCode = deviceResponseCode;
    }

    public String getDeviceResponseText() {
        return deviceResponseText;
    }

    public void setDeviceResponseText(String deviceResponseText) {
        this.deviceResponseText = deviceResponseText;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
}