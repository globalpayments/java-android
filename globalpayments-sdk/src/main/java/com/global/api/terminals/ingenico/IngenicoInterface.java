package com.global.api.terminals.ingenico;

import com.global.api.entities.exceptions.ApiException;
import com.global.api.entities.exceptions.UnsupportedTransactionException;
import com.global.api.terminals.abstractions.*;
import com.global.api.terminals.builders.*;
import com.global.api.terminals.ingenico.variables.PaymentType;
import com.global.api.terminals.messaging.IBroadcastMessageInterface;
import com.global.api.terminals.messaging.IMessageReceivedInterface;
import com.global.api.terminals.messaging.IMessageSentInterface;
import com.global.api.terminals.*;

import java.math.BigDecimal;

public class IngenicoInterface extends DeviceInterface<IngenicoController> implements IDeviceInterface {
    private PaymentType paymentMethod = null;

    public PaymentType getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentType paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    IngenicoInterface(IngenicoController controller) {
        super(controller);
    }

    public void setOnMessageSent(IMessageSentInterface onMessageSent) {
        this.onMessageSent = onMessageSent;
    }

    public void setOnBroadcastMessageReceived(IBroadcastMessageInterface onBroadcastMessageReceived) {
        this.onBroadcastMessage = onBroadcastMessageReceived;
    }

    public void setOnMessageReceived(IMessageReceivedInterface onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    @Override
    public void cancel() throws ApiException {

    }

    @Override
    public TerminalManageBuilder cancel(BigDecimal amount) throws UnsupportedTransactionException {
        if (amount != null)
            return super.cancel(amount);
        else throw new UnsupportedTransactionException("Amount can't be null");
    }

    public TerminalAuthBuilder sale(BigDecimal amount) throws UnsupportedTransactionException {
        paymentMethod = PaymentType.SALE;
        return super.sale(amount);
    }

    public TerminalAuthBuilder refund(BigDecimal amount) throws UnsupportedTransactionException {
        paymentMethod = PaymentType.REFUND;
        return super.refund(amount);
    }

    public TerminalManageBuilder capture(BigDecimal amount) throws UnsupportedTransactionException {
        paymentMethod = PaymentType.COMPLETION;
        return super.capture(amount);
    }

    public TerminalAuthBuilder authorize(BigDecimal amount) throws UnsupportedTransactionException {
        paymentMethod = PaymentType.PREAUTH;
        return super.authorize(amount);
    }

    public TerminalAuthBuilder verify() throws UnsupportedTransactionException {
        paymentMethod = PaymentType.ACCOUNT_VERIFICATION;
        return super.verify();
    }
}