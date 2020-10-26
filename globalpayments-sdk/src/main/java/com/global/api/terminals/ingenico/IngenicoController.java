package com.global.api.terminals.ingenico;

import android.util.Log;

import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;

import com.global.api.entities.enums.ConnectionModes;
import com.global.api.entities.enums.ControlCodes;
import com.global.api.entities.enums.TransactionType;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.entities.exceptions.BuilderException;
import com.global.api.entities.exceptions.ConfigurationException;
import com.global.api.terminals.DeviceController;
import com.global.api.terminals.TerminalUtilities;
import com.global.api.terminals.abstractions.IDeviceCommInterface;
import com.global.api.terminals.abstractions.IDeviceInterface;
import com.global.api.terminals.abstractions.IDeviceMessage;
import com.global.api.terminals.abstractions.ITerminalConfiguration;
import com.global.api.terminals.abstractions.ITerminalReport;
import com.global.api.terminals.abstractions.ITerminalResponse;
import com.global.api.terminals.builders.TerminalAuthBuilder;
import com.global.api.terminals.builders.TerminalManageBuilder;
import com.global.api.terminals.builders.TerminalReportBuilder;
import com.global.api.terminals.ingenico.interfaces.IngenicoTcpInterface;
import com.global.api.terminals.ingenico.responses.IngenicoTerminalReceiptResponse;
import com.global.api.terminals.ingenico.responses.IngenicoTerminalReportResponse;
import com.global.api.terminals.ingenico.responses.IngenicoTerminalResponse;
import com.global.api.terminals.ingenico.responses.ReverseResponse;
import com.global.api.terminals.ingenico.variables.INGENICO_REQ_CMD;
import com.global.api.terminals.ingenico.variables.INGENICO_RESP;
import com.global.api.terminals.ingenico.variables.PATResponseType;
import com.global.api.terminals.ingenico.variables.ParseFormat;
import com.global.api.terminals.ingenico.variables.PaymentMode;
import com.global.api.terminals.ingenico.variables.PaymentType;
import com.global.api.terminals.ingenico.variables.TransactionStatus;
import com.global.api.utils.Extensions;

public class IngenicoController extends DeviceController {
    private IDeviceInterface _device;

    public IngenicoController(ITerminalConfiguration settings) throws ConfigurationException {
        super(settings);
    }

    @Override
    public IDeviceInterface configureInterface() throws ConfigurationException {
        if (_device == null) {
            _device = new IngenicoInterface(this);
        }
        return _device;
    }

    @Override
    public IDeviceCommInterface configureConnector() throws ConfigurationException {
        switch (settings.getConnectionMode()) {
            case TCP_IP_SERVER:
            case PAY_AT_TABLE:
                return new IngenicoTcpInterface(settings);
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public ITerminalResponse manageTransaction(TerminalManageBuilder builder) throws ApiException {
        IDeviceMessage request = buildManageTransaction(builder);
        if (builder.getTransactionType() == TransactionType.Reversal) {
            return doReverseRequest(request);
        } else {
            return doRequest(request);
        }
    }

    @Override
    public ITerminalReport processReport(TerminalReportBuilder builder) throws ApiException {
        IDeviceMessage request;
        if (!isObjectNullOrEmpty(builder.getType())) {
            request = buildReportTransaction(builder);
            return reportRequest(request);
        } else {
            request = TerminalUtilities.buildIngenicoRequest(
                    String.format(new INGENICO_REQ_CMD().RECEIPT, builder.getReceiptType()),
                    settings.getConnectionMode());
            return receiptRequest(request);
        }
    }

    @Override
    public ITerminalResponse processTransaction(TerminalAuthBuilder builder) throws ApiException {
        IDeviceMessage request = null;
        if (settings.getConnectionMode() == ConnectionModes.PAY_AT_TABLE) {
            request = buildPATTResponseMessage(builder);
        } else {
            request = buildRequestMessage(builder);
        }
        return doRequest(request);
    }

    private byte[] getXMLContent(String xmlPath) throws BuilderException {
        byte[] result;
        String xmlContent;
        byte[] xmlByteArr;

        try {
            if (xmlPath.isEmpty()) {
                throw new BuilderException("XML Path is Empty");
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                xmlByteArr = Files.readAllBytes(Paths.get(xmlPath));
            } else {
                RandomAccessFile xml = new RandomAccessFile(xmlPath, "r");
                byte[] b = new byte[(int) xml.length()];
                xml.readFully(b);
                xmlByteArr = b;
            }
            xmlContent = new String(xmlByteArr, StandardCharsets.UTF_8);
            result = xmlContent.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new BuilderException(e.getMessage());
        }

        return result;
    }

    private IDeviceMessage buildReportTransaction(TerminalReportBuilder builder) throws BuilderException {
        if (!isObjectNullOrEmpty(builder.getType())) {
            String message = Extensions.formatWith(new INGENICO_REQ_CMD().REPORT, builder.getType());
            return TerminalUtilities.buildIngenicoRequest(message, settings.getConnectionMode());
        } else {
            throw new BuilderException("Type of report is missing in request.");
        }
    }

    private IDeviceMessage buildManageTransaction(TerminalManageBuilder builder) throws BuilderException {
        Integer referenceNumber = builder.getReferenceNumber();
        BigDecimal amount = validateAmount(builder.getAmount());
        Integer returnRep = 1;
        Integer paymentMode = 0;
        Integer paymentType = ((IngenicoInterface) _device).getPaymentMethod() == null ? 0
                : ((IngenicoInterface) _device).getPaymentMethod().getValue();
        String currencyCode = "826";
        String privateData = "EXT0100000";
        String immediateAnswer = "A010";
        String forceOnline = "B010";
        String extendedData = "0000000000";

        if (!isObjectNullOrEmpty(builder.getAuthCode())) {
            extendedData = Extensions.formatWith(new INGENICO_REQ_CMD().AUTHCODE, builder.getAuthCode());
        } else if (!isObjectNullOrEmpty(builder.getTransactionId())
                && builder.getTransactionType() == TransactionType.Reversal) {
            extendedData = Extensions.formatWith(new INGENICO_REQ_CMD().REVERSE_WITH_ID, builder.getTransactionId());
        } else if (builder.getTransactionType() == TransactionType.Reversal) {
            extendedData = new INGENICO_REQ_CMD().REVERSE;
        }

        DecimalFormat decimalFormat = new DecimalFormat("00000000");
        StringBuilder message = new StringBuilder();
        message.append(String.format("%02d", referenceNumber));
        message.append(decimalFormat.format(amount));
        message.append(returnRep);
        message.append(paymentMode);
        message.append(paymentType);
        message.append(currencyCode);
        message.append(privateData);
        message.append(immediateAnswer);
        message.append(forceOnline);
        message.append(extendedData);

        return TerminalUtilities.buildIngenicoRequest(message.toString(), settings.getConnectionMode());
    }

    private IDeviceMessage buildRequestMessage(TerminalAuthBuilder builder) throws BuilderException {
        Integer referenceNumber = builder.getReferenceNumber();
        BigDecimal amount = builder.getAmount();
        Integer returnRep = 1;
        Integer paymentMode = 0;
        Integer paymentType = ((IngenicoInterface) _device).getPaymentMethod().getValue();
        String currencyCode = "826";
        String privateData = "EXT0100000";
        String immediateAnswer = "A010";
        String forceOnline = "B010";
        String extendedData = "0000000000";

        BigDecimal cashbackAmount = builder.getCashBackAmount();
        String authCode = builder.getAuthCode();
        String tableId = builder.getTableNumber();

        if (!isObjectNullOrEmpty(requestIdProvider())) {
            referenceNumber = requestIdProvider().getRequestId();
        }

        if (!isObjectNullOrEmpty(builder.getTaxFreeType()) && paymentType == PaymentType.REFUND.getValue()) {
            Integer taxFree = builder.getTaxFreeType().toInteger();
            PaymentType[] type = PaymentType.values();
            for (PaymentType p : type) {
                Integer typeValue = p.ordinal();
                if (typeValue == taxFree) {
                    paymentType = typeValue;
                    break;
                }
            }
        }

        amount = validateAmount(amount);
        paymentMode = validatePaymentMode(builder.getPaymentMode());
        currencyCode = (!isObjectNullOrEmpty(builder.getCurrencyCode()) ? builder.getCurrencyCode() : currencyCode);

        if (!isObjectNullOrEmpty(tableId)) {
            validateTableReference(tableId);
            extendedData = Extensions.formatWith(new INGENICO_REQ_CMD().TABLE_WITH_ID, tableId);
        } else if (!isObjectNullOrEmpty(authCode)) {
            extendedData = Extensions.formatWith(new INGENICO_REQ_CMD().AUTHCODE, authCode);
        } else if (!isObjectNullOrEmpty(cashbackAmount)) {
            cashbackAmount = validateCashbackAmount(cashbackAmount.toString());
            extendedData = Extensions.formatWith(new INGENICO_REQ_CMD().CASHBACK, cashbackAmount);
        }

        DecimalFormat decimalFormat = new DecimalFormat("00000000");
        StringBuilder message = new StringBuilder();
        message.append(String.format("%02d", referenceNumber));
        message.append(decimalFormat.format(amount));
        message.append(returnRep);
        message.append(paymentMode);
        message.append(paymentType);
        message.append(currencyCode);
        message.append(privateData);
        message.append(immediateAnswer);
        message.append(forceOnline);
        message.append(extendedData);

        return TerminalUtilities.buildIngenicoRequest(message.toString(), settings.getConnectionMode());
    }

    private IDeviceMessage buildPATTResponseMessage(TerminalAuthBuilder builder) throws BuilderException {
        StringBuilder message = new StringBuilder();

        // PAT Functionalities
        if (builder.getXMLPath() != null) {
            byte[] content = getXMLContent(builder.getXMLPath());
            String xml = new String(content, StandardCharsets.ISO_8859_1);
            message.append(xml);
        } else {
            String referenceNumber = new INGENICO_RESP().PAT_EPOS_NUMBER;
            Integer transactionStatus = TransactionStatus.SUCCESS.getValue();
            BigDecimal amount = validateAmount(builder.getAmount());
            Integer paymentMode = builder.getPATTPaymentMode().getValue();
            String currencyCode = builder.getCurrencyCode();
            String privateData = PATResponseType.getEnumName(builder.getPATTResponseType().getValue()).toString();

            if (privateData.length() < 10) {
                for (int i = privateData.length(); i < 10; i++) {
                    privateData += (char) ControlCodes.SP.getByte();
                }
            }
            currencyCode = (!isObjectNullOrEmpty(builder.getCurrencyCode()) ? builder.getCurrencyCode() : currencyCode);

            DecimalFormat decimalFormat = new DecimalFormat("00000000");
            message.append(referenceNumber);
            message.append(transactionStatus);
            message.append(decimalFormat.format(amount));
            message.append(paymentMode);
            message.append(currencyCode);
            message.append(privateData);

            Log.i("MESSAGE", decimalFormat.format(amount));
        }

        return TerminalUtilities.buildIngenicoRequest(message.toString(), settings.getConnectionMode());
    }

    private static boolean isObjectNullOrEmpty(Object value) {
        boolean response = false;

        if (value == null || value.toString().isEmpty()) {
            response = true;
        }

        return response;
    }

    private static void validateTableReference(String value) throws BuilderException {
        if (value.length() > 8) {
            throw new BuilderException("The maximum length of table number is 8.");
        }
    }

    private static Integer validatePaymentMode(PaymentMode paymentMode) {
        if (paymentMode == null) {
            paymentMode = PaymentMode.APPLICATION;
        }

        return paymentMode.getValue();
    }

    private static BigDecimal validateCashbackAmount(String value) throws BuilderException {
        BigDecimal cashbackAmount = new BigDecimal(value);

        BigDecimal amount1hun = new BigDecimal("100");
        if (cashbackAmount == null) {
            throw new BuilderException("Cashback Amount must not be less than or equal to 0.");
        } else if ((cashbackAmount.compareTo(BigDecimal.ZERO) > 0) && (cashbackAmount.compareTo(amount1hun) <= 0)) {
            cashbackAmount = cashbackAmount.multiply(new BigDecimal("100"));
            cashbackAmount = cashbackAmount.setScale(0, BigDecimal.ROUND_HALF_EVEN);
        } else if (cashbackAmount.compareTo(amount1hun) > 0) {
            throw new BuilderException("Cashback Amount exceeded.");
        } else {
            throw new BuilderException("Invalid input amount.");
        }

        return cashbackAmount;
    }

    private static BigDecimal validateAmount(BigDecimal amount) throws BuilderException {
        BigDecimal amount1mil = new BigDecimal("1000000");

        if (amount == null) {
            throw new BuilderException("Amount cannot be null.");
        } else if ((amount.compareTo(BigDecimal.ZERO) > 0) && (amount.compareTo(amount1mil) < 0)) {
            amount = amount.multiply(new BigDecimal("100"));
        } else if ((amount.compareTo(amount1mil) == 0) && (amount.compareTo(amount1mil) > 0)) {
            throw new BuilderException("Amount exceeded.");
        } else {
            throw new BuilderException("Invalid input amount.");
        }

        return amount;
    }

    public ITerminalConfiguration getConfiguration() {
        return settings;
    }

    private IngenicoTerminalReceiptResponse receiptRequest(IDeviceMessage request) throws ApiException {
        byte[] send = send(request);
        return new IngenicoTerminalReceiptResponse(send);
    }

    private IngenicoTerminalReportResponse reportRequest(IDeviceMessage request) throws ApiException {
        byte[] send = send(request);
        return new IngenicoTerminalReportResponse(send);
    }

    private IngenicoTerminalResponse doRequest(IDeviceMessage request) throws ApiException {
        byte[] response = send(request);
        return new IngenicoTerminalResponse(response, ParseFormat.Transaction);
    }

    private ReverseResponse doReverseRequest(IDeviceMessage request) throws ApiException {
        byte[] response = send(request);
        return new ReverseResponse(response);
    }
}
