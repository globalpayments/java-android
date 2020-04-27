package com.global.api.terminals;

import java.math.BigDecimal;

import com.global.api.entities.enums.PaymentMethodType;
import com.global.api.entities.enums.SendFileType;
import com.global.api.entities.enums.TransactionType;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.entities.exceptions.UnsupportedTransactionException;
//import com.global.api.terminals.abstractions.IBatchCloseResponse;
import com.global.api.terminals.abstractions.IDeviceInterface;
import com.global.api.terminals.abstractions.IDeviceResponse;
//import com.global.api.terminals.abstractions.IEODResponse;
import com.global.api.terminals.abstractions.IInitializeResponse;
//import com.global.api.terminals.abstractions.ISAFResponse;
//import com.global.api.terminals.abstractions.ISignatureResponse;
import com.global.api.terminals.builders.TerminalAuthBuilder;
import com.global.api.terminals.builders.TerminalManageBuilder;
import com.global.api.terminals.builders.TerminalReportBuilder;
import com.global.api.terminals.ingenico.variables.ReceiptType;
import com.global.api.terminals.ingenico.variables.ReportTypes;
import com.global.api.terminals.messaging.IBroadcastMessageInterface;
import com.global.api.terminals.messaging.IMessageReceivedInterface;
import com.global.api.terminals.messaging.IMessageSentInterface;

public abstract class DeviceInterface<T extends DeviceController> implements IDeviceInterface {
    protected T _controller;
    protected IRequestIdProvider _requestIdProvider;

    public IMessageSentInterface onMessageSent;
    public IBroadcastMessageInterface onBroadcastMessage;
    public IMessageReceivedInterface onMessageReceived;

    public void setOnMessageSentHandler(IMessageSentInterface onMessageSent) {
        this.onMessageSent = onMessageSent;
    }

    public void setOnBroadcastMessageHandler(IBroadcastMessageInterface onBroadcastMessage) {
        this.onBroadcastMessage = onBroadcastMessage;
    }

    public DeviceInterface(T controller) {
        _controller = controller;
        _controller.setOnMessageSentHandler(new IMessageSentInterface() {
            public void messageSent(String message) {
                if (onMessageSent != null) {
                    onMessageSent.messageSent(message);
                }
            }
        });

        _controller.setOnBroadcastMessageHandler(new IBroadcastMessageInterface() {
            public void broadcastReceived(String code, String message) {
                if (onBroadcastMessage != null) {
                    onBroadcastMessage.broadcastReceived(code, message);
                }
            }
        });

        _controller.setOnMessageReceivedHandler(new IMessageReceivedInterface() {
            @Override
            public void messageReceived(String message) {
                if (onMessageReceived != null) {
                    onMessageReceived.messageReceived(message);
                }
            }
        });

        _requestIdProvider = _controller.requestIdProvider();
    }

    // admin methods
    public IDeviceResponse closeLane() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public IDeviceResponse disableHostResponseBeep() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

//    public ISignatureResponse getSignatureFile() throws UnsupportedTransactionException {
//        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
//    }

    public IInitializeResponse initialize() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public IDeviceResponse addLineItem(String leftText, String rightText, String runningLeftText,
                                       String runningRightText) throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public IDeviceResponse openLane() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

//    public ISignatureResponse promptForSignature(String transactionId) throws UnsupportedTransactionException {
//        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
//    }

    public IDeviceResponse reboot() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public IDeviceResponse reset() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public IDeviceResponse sendFile(SendFileType fileType, String filePath) throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

//    public ISAFResponse sendStoreAndForward() throws UnsupportedTransactionException {
//        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
//    }

    public IDeviceResponse setStoreAndForwardMode(boolean enabled) throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public IDeviceResponse startCard(PaymentMethodType paymentMethodType) throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

//    public IEODResponse endOfDay() throws UnsupportedTransactionException {
//        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
//    }

    // batching
//    public IBatchCloseResponse batchClose() throws UnsupportedTransactionException {
//        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
//    }

    // credit calls
    public TerminalAuthBuilder creditAuth(BigDecimal amount) throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalManageBuilder creditCapture(BigDecimal amount) throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder creditRefund(BigDecimal amount) throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder creditSale(BigDecimal amount) throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder creditAuth() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalManageBuilder creditCapture() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder creditRefund() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder creditSale() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder creditVerify() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalManageBuilder creditVoid() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    // debit calls
    public TerminalAuthBuilder debitSale(BigDecimal amount) throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder debitRefund(BigDecimal amount) throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder debitSale() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder debitRefund() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    // gift calls
    public TerminalAuthBuilder giftSale(BigDecimal amount) throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder giftSale() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder giftAddValue() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder giftAddValue(BigDecimal amount) throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalManageBuilder giftVoid() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder giftBalance() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    // ebt calls
    public TerminalAuthBuilder ebtBalance() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder ebtPurchase() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder ebtPurchase(BigDecimal amount) throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder ebtRefund() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder ebtRefund(BigDecimal amount) throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder ebtWithdrawal() throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    public TerminalAuthBuilder ebtWithdrawal(BigDecimal amount) throws UnsupportedTransactionException {
        throw new UnsupportedTransactionException("This function is not supported by the currently configured device.");
    }

    // generic calls
    public TerminalAuthBuilder authorize(BigDecimal amount) throws UnsupportedTransactionException {
        return new TerminalAuthBuilder(TransactionType.Auth, PaymentMethodType.Credit)
                .withAmount(amount);
    }

    public TerminalManageBuilder capture(BigDecimal amount) throws UnsupportedTransactionException {
        return new TerminalManageBuilder(TransactionType.Capture, PaymentMethodType.Credit)
                .withAmount(amount);
    }

    public TerminalAuthBuilder refund(BigDecimal amount) throws UnsupportedTransactionException {
        return new TerminalAuthBuilder(TransactionType.Refund, PaymentMethodType.Credit)
                .withAmount(amount);
    }

    public TerminalAuthBuilder sale(BigDecimal amount) throws UnsupportedTransactionException {
        return new TerminalAuthBuilder(TransactionType.Sale, PaymentMethodType.Credit)
                .withAmount(amount);
    }

    public TerminalAuthBuilder verify() throws UnsupportedTransactionException {
        return new TerminalAuthBuilder(TransactionType.Verify, PaymentMethodType.Credit)
                .withAmount(new BigDecimal(0.01));
    }

    public TerminalAuthBuilder getReport(ReportTypes type) throws UnsupportedTransactionException {
        return new TerminalAuthBuilder(TransactionType.Create, PaymentMethodType.Other).withReportType(type);
    }

    public TerminalReportBuilder getLastReceipt(ReceiptType type) throws UnsupportedTransactionException {
        return new TerminalReportBuilder(type);
    }

    public TerminalManageBuilder duplicate(BigDecimal amount) throws UnsupportedTransactionException {
        return new TerminalManageBuilder(TransactionType.Duplicate, PaymentMethodType.Credit).withAmount(amount);
    }

    public TerminalManageBuilder reverse(BigDecimal amount) throws UnsupportedTransactionException {
        return new TerminalManageBuilder(TransactionType.Reversal, PaymentMethodType.Credit).withAmount(amount);
    }

    public TerminalManageBuilder cancel(BigDecimal amount) throws UnsupportedTransactionException {
        return new TerminalManageBuilder(TransactionType.Cancel, PaymentMethodType.Credit).withAmount(amount);
    }

    // dispose
    public void dispose() {
        _controller.dispose();
    }
}
