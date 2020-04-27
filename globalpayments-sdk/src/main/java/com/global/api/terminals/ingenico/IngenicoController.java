package com.global.api.terminals.ingenico;

import com.global.api.entities.enums.TransactionType;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.entities.exceptions.BuilderException;
import com.global.api.entities.exceptions.ConfigurationException;
import com.global.api.entities.exceptions.MessageException;
import com.global.api.terminals.DeviceController;
import com.global.api.terminals.DeviceMessage;
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
import com.global.api.terminals.ingenico.responses.CancelResponse;
import com.global.api.terminals.ingenico.responses.IngenicoTerminalReportResponse;
import com.global.api.terminals.ingenico.responses.IngenicoTerminalResponse;
import com.global.api.terminals.ingenico.responses.ReverseResponse;
import com.global.api.terminals.ingenico.variables.ExtendedDataTags;
import com.global.api.terminals.ingenico.variables.INGENICO_REQ_CMD;
import com.global.api.terminals.ingenico.variables.PaymentMode;
import com.global.api.terminals.messaging.IBroadcastMessageInterface;
import com.global.api.terminals.messaging.IMessageReceivedInterface;
import com.global.api.terminals.messaging.IMessageSentInterface;
import com.global.api.utils.Extensions;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

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
            case SERIAL:
                throw new ConfigurationException("Serial is not available for this library.");
            case TCP_IP_SERVER:
                try {
                    return new IngenicoTcpInterface(settings);
                } catch (ConfigurationException e) {
                    e.printStackTrace();
                    throw e;
                }
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public ITerminalResponse manageTransaction(TerminalManageBuilder builder) throws ApiException {
        IDeviceMessage request = BuildManageTransaction(builder);
        if (builder.getTransactionType() == TransactionType.Cancel) {
            return doCancelRequest(request);
        } else if (builder.getTransactionType() == TransactionType.Reversal) {
            return doReverseRequest(request);
        } else {
            return doRequest(request);
        }
    }

    @Override
    public ITerminalReport processReport(TerminalReportBuilder builder) throws ApiException {
        DeviceMessage request = TerminalUtilities.buildIngenicoRequest(
                String.format(new INGENICO_REQ_CMD().RECEIPT, builder.getReceiptType()), settings.getConnectionMode());
        return reportRequest(request);
    }

    @Override
    public ITerminalResponse processTransaction(TerminalAuthBuilder builder) throws ApiException {
        IDeviceMessage request = BuildRequestMessage(builder);
        return doRequest(request);
    }

//	private byte[] SerializeRequest(TerminalAuthBuilder builder) throws BuilderException {
//		return buildRequestMessage(builder).getSendBuffer();
//	}
//
//	private byte[] SerializeRequest(TerminalManageBuilder builder) {
//		throw new UnsupportedOperationException();
//	}
//
//	private byte[] SerializeRequest(TerminalReportBuilder builder) {
//		throw new UnsupportedOperationException();
//	}

    private IDeviceMessage BuildManageTransaction(TerminalManageBuilder builder) throws BuilderException {
        DecimalFormat decimalFormat = new DecimalFormat("00000000");

        Integer _referenceNumber = builder.getReferenceNumber();
        BigDecimal _amount = ValidateAmount(builder.getAmount());
        int _returnRep = 1;
        int _paymentMode = 0;
        int _paymentType = ((IngenicoInterface) _device).getPaymentMethod().getPaymentType();
        String _currencyCode = "826";
        String _privateData = "EXT0100000";
        int _immediateAnswer = 0;
        int _forceOnline = 0;
        String _extendedData = "0000000000";

        if (!IsObjectNullOrEmpty(builder.getAuthCode()))
            _extendedData = ValidateExtendedData(builder.getAuthCode(), builder.getExtendedDataTag());
        else if (builder.getTransactionId() != null && builder.getTransactionType() == TransactionType.Reversal)
            _extendedData = ValidateExtendedData(builder.getTransactionId(), ExtendedDataTags.TXN_COMMANDS_PARAMS);
        else
            _extendedData = ValidateExtendedData(builder.getTransactionType().toString(),
                    ExtendedDataTags.TXN_COMMANDS);

        String message = String.format("%s%s%s%s%s%s%sA01%sB01%s%s", String.format("%02d", _referenceNumber),
                decimalFormat.format(_amount), _returnRep, _paymentMode, _paymentType, _currencyCode, _privateData,
                _immediateAnswer, _forceOnline, _extendedData);

        return TerminalUtilities.buildIngenicoRequest(message, settings.getConnectionMode());
    }

    private IDeviceMessage BuildRequestMessage(TerminalAuthBuilder builder) throws BuilderException {
        String message = "";
        DecimalFormat decimalFormat = new DecimalFormat("00000000");

        if (!IsObjectNullOrEmpty(builder.getReportType())) {
            message = String.format(new INGENICO_REQ_CMD().REPORT, builder.getReportType());
        } else {
            Integer _referenceNumber = builder.getReferenceNumber();
            BigDecimal _amount = builder.getAmount();
            int _returnRep = 1;
            int _paymentMode = 0;
            Integer _paymentType = ((IngenicoInterface) _device).getPaymentMethod().getPaymentType();
            String _currencyCode = "826";
            String _privateData = "EXT0100000";
            int _immediateAnswer = 0;
            int _forceOnline = 0;
            String _extendedData = "0000000000";

            BigDecimal _cashbackAmount = builder.getCashBackAmount();
            String _authCode = builder.getAuthCode();
            String tableId = builder.getTableNumber();

            // Validations
            _amount = ValidateAmount(_amount);
            _paymentMode = ValidatePaymentMode(builder.getPaymentMode());
            _currencyCode = (IsObjectNullOrEmpty(builder.getCurrencyCode()) ? _currencyCode
                    : builder.getCurrencyCode());

            if (!IsObjectNullOrEmpty(tableId)) {
                boolean validateTableId = ValidateTableReference(tableId);
                if (validateTableId) {
                    _extendedData = ValidateExtendedData(tableId, builder.getExtendedDataTag());
                }
            }

            if (!IsObjectNullOrEmpty(_cashbackAmount)) {
                _extendedData = ValidateExtendedData(_cashbackAmount.toString(), builder.getExtendedDataTag());
            } else if (!IsObjectNullOrEmpty(_authCode)) {
                _extendedData = ValidateExtendedData(_authCode, builder.getExtendedDataTag());
            }

            message = String.format("%s%s%s%s%s%s%sA01%sB01%s%s", String.format("%02d", _referenceNumber),
                    decimalFormat.format(_amount), _returnRep, _paymentMode, _paymentType, _currencyCode, _privateData,
                    _immediateAnswer, _forceOnline, _extendedData);
        }

        return TerminalUtilities.buildIngenicoRequest(message, settings.getConnectionMode());
    }

    private static boolean IsObjectNullOrEmpty(Object value) {
        boolean response = false;
        if (value == null || value.toString().isEmpty())
            response = true;
        else
            response = false;

        return response;
    }

    private static boolean ValidateTableReference(String value) throws BuilderException {
        boolean response = false;
        if (!(value.equals(null)) && value.length() <= 8)
            response = true;
        else
            throw new BuilderException("Table number must not be less than or equal 0 or greater than 8 numerics.");

        return response;
    }

    private static int ValidatePaymentMode(PaymentMode _paymentMode) {
        if (_paymentMode == null)
            _paymentMode = PaymentMode.APPLICATION;
        return _paymentMode.getPaymentMode();
    }

    private static String ValidateExtendedData(String value, ExtendedDataTags tags) throws BuilderException {
        String extendedData = "0000000000";

        if (!IsObjectNullOrEmpty(value))
            switch (tags) {
                case CASHB:

                    BigDecimal cashbackAmount = new BigDecimal(value);

                    BigDecimal amount1hun = new BigDecimal("100");
                    if (cashbackAmount == null) {
                        throw new BuilderException("Cashback Amount must not be in less than or equal 0 value.");
                    } else if ((cashbackAmount.compareTo(BigDecimal.ZERO) > 0) && (cashbackAmount.compareTo(amount1hun) <= 0)) {
                        cashbackAmount = cashbackAmount.multiply(new BigDecimal("100"));
                        cashbackAmount = cashbackAmount.setScale(0, BigDecimal.ROUND_HALF_EVEN);
                    } else if (cashbackAmount.compareTo(amount1hun) > 0) {
                        throw new BuilderException("Cashback Amount exceed.");
                    } else {
                        throw new BuilderException("Invalid input amount.");
                    }

                    extendedData = Extensions.FormatWith("CASHB=%s;", cashbackAmount);
                    break;
                case AUTHCODE:
                    extendedData = Extensions.FormatWith("AUTHCODE=%s;", value);
                    break;
                case TABLE_NUMBER:
                    extendedData = Extensions.FormatWith("CMD=ID%s;", value);
                    break;
                case TXN_COMMANDS:
                    TransactionType transType = TransactionType.valueOf(value);
                    switch (transType) {
                        case Cancel:
                            extendedData = new INGENICO_REQ_CMD().CANCEL;
                            break;
                        case Duplicate:
                            extendedData = new INGENICO_REQ_CMD().DUPLICATE;
                            break;
                        case Reversal:
                            extendedData = new INGENICO_REQ_CMD().REVERSE;
                            break;
                    }
                    break;
                case TXN_COMMANDS_PARAMS:
                    extendedData = Extensions.FormatWith(new INGENICO_REQ_CMD().REVERSE_WITH_ID, value);
                    break;
            }

        return extendedData;
    }

    private static BigDecimal ValidateAmount(BigDecimal _amount) throws BuilderException {
        BigDecimal amount1mil = new BigDecimal("1000000");
        if (_amount == null) {
            throw new BuilderException("Amount can not be null.");
        } else if ((_amount.compareTo(BigDecimal.ZERO) > 0) && (_amount.compareTo(amount1mil) < 0)) {
            _amount = _amount.multiply(new BigDecimal("100"));
            _amount = _amount.setScale(0, BigDecimal.ROUND_HALF_EVEN);
        } else if ((_amount.compareTo(amount1mil) == 0) || (_amount.compareTo(amount1mil) > 0)) {
            throw new BuilderException("Amount exceed.");
        } else {
            throw new BuilderException("Invalid input amount.");
        }
        return _amount;
    }

    public ITerminalConfiguration GetConfiguration() {
        return settings;
    }

    private IngenicoTerminalReportResponse reportRequest(IDeviceMessage request) throws ApiException {
        byte[] send = send(request);
        return new IngenicoTerminalReportResponse(send);
    }

    private IngenicoTerminalResponse doRequest(IDeviceMessage request) throws ApiException {
        byte[] response = send(request);
        return new IngenicoTerminalResponse(response);
    }

    private CancelResponse doCancelRequest(IDeviceMessage request) throws ApiException {
        byte[] response = send(request);
        return new CancelResponse(response);
    }

    private ReverseResponse doReverseRequest(IDeviceMessage request) throws ApiException {
        byte[] response = send(request);
        return new ReverseResponse(response);
    }
}
