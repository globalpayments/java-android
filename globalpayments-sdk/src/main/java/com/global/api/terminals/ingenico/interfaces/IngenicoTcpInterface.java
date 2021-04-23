package com.global.api.terminals.ingenico.interfaces;

import android.os.Environment;
import android.util.Log;

import com.global.api.entities.enums.ConnectionModes;
import com.global.api.entities.enums.ControlCodes;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.entities.exceptions.ConfigurationException;
import com.global.api.services.DeviceService;
import com.global.api.terminals.ConnectionConfig;
import com.global.api.terminals.TerminalUtilities;
import com.global.api.terminals.abstractions.IDeviceCommInterface;
import com.global.api.terminals.abstractions.IDeviceInterface;
import com.global.api.terminals.abstractions.IDeviceMessage;
import com.global.api.terminals.abstractions.IDeviceResponse;
import com.global.api.terminals.abstractions.ITerminalConfiguration;
import com.global.api.terminals.ingenico.pat.PATRequest;
import com.global.api.terminals.ingenico.responses.BroadcastMessage;
import com.global.api.terminals.ingenico.variables.INGENICO_GLOBALS;
import com.global.api.terminals.messaging.IBroadcastMessageInterface;
import com.global.api.terminals.messaging.IMessageSentInterface;
import com.global.api.terminals.messaging.IOnPayAtTableRequestInterface;
import com.global.api.utils.MessageWriter;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import static java.nio.charset.StandardCharsets.*;

public class IngenicoTcpInterface implements IDeviceCommInterface {
    public String lastErrorMsg = "";
    public Object lock;
    private boolean isNotified;

    private ServerSocket _serverSocket;
    private Socket _socket;
    private ITerminalConfiguration _settings;
    private byte[] _terminalResponse;
    private Thread dataReceiving;
    private boolean _isKeepAlive;
    private Exception _receivingException;
    private BroadcastMessage _broadcastMessage;
    private boolean _isResponseNeeded = false;
    private DataOutputStream _out;
    private DataInputStream _in;
    private String logData = "";

    private boolean _readData;

    private IBroadcastMessageInterface onBroadcastMessage;
    private IMessageSentInterface onMessageSent;
    private IOnPayAtTableRequestInterface _onPayAtTableRequest;

    public IngenicoTcpInterface(ITerminalConfiguration settings) throws ConfigurationException {
        try {
            this._settings = settings;
            this._socket = new Socket();
            connect();

        } catch (ConfigurationException e) {
            throw e;
        }
    }

    //region Override methods
    @Override
    public void connect() throws ConfigurationException {
        try {
            // Start Server socket
            initializeServer();

            // Accept client
            acceptClient();

            // Start thread for Receiving data.
            if (dataReceiving == null) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            analyzeReceivedData();
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

        } catch (ConfigurationException | IOException e) {
            throw new ConfigurationException(e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        try {
            if (_serverSocket != null || !_serverSocket.isClosed()) {
                if (!_isKeepAlive) {
                    _socket.setSoTimeout(1000);
                }

                _readData = false;
                _in.close();
                _out.close();
                _socket.close();
                _serverSocket.close();
                System.out.println("Server successfully stopped.");
            }
            _serverSocket = null;
        } catch (IOException e) {
            // Eating the close exception
        }
    }

    public void appendLog(String text)
    {
        File logFile = new File(Environment.getExternalStorageDirectory(), "POC_LOGGER.log");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private byte[] removeHeader(byte[] buffer) {
        return Arrays.copyOfRange(buffer, 2, buffer.length);
    }

    @Override
    public byte[] send(IDeviceMessage message) throws ApiException {
//        byte[] buffer = message.getSendBuffer();
//        _terminalResponse = null;
//        _receivingException = null;
//        _isResponseNeeded = true;
//
//        try {
//            if (_serverSocket == null) {
//                throw new ConfigurationException("Server is not running.");
//            } else if (_socket == null) {
//                _socket = _serverSocket.accept();
//            }
//
//            _out.write(buffer);
//            _out.flush();
//
//            if (_settings.getConnectionMode() == ConnectionModes.PAY_AT_TABLE) {
//                String data = TerminalUtilities.getString(buffer);
//
//                if (onMessageSent != null) {
//                    String messageSent = data.substring(1, data.length() - 3);
//                    onMessageSent.messageSent(messageSent);
//                }
//
//                return null;
//            }
//
//            if (onMessageSent != null) {
//                String messageSent = TerminalUtilities.getString(removeHeader(buffer));
//                onMessageSent.messageSent(messageSent);
//            }
//
//            while (_terminalResponse == null) {
//                Thread.sleep(100);
//                if (_receivingException != null) {
//                    throw new ApiException(_receivingException.getMessage());
//                }
//
//                if (_terminalResponse != null) {
//                    _isResponseNeeded = false;
//                    return _terminalResponse;
//                }
//            }
//        } catch (Exception e) {
//            throw new ApiException(e.getMessage());
//        }
//
//        return _terminalResponse;

        lastErrorMsg = null;
        _terminalResponse = null;
        _isResponseNeeded = true;

        final byte[] buffer = message.getSendBuffer();

        try {
            if (_serverSocket == null) {
                throw new ConfigurationException("Error: Server is not running.");
            }

            // Send request from builder.
//            _socket.setSoTimeout(_settings.getTimeout());
            _out.write(buffer, 0, buffer.length);
            _out.flush();

            onMessageSent.messageSent(TerminalUtilities.getString(buffer).substring(2));

            synchronized (lock) {
                while (_terminalResponse == null) {
//                    Thread.sleep(10);

//                    if (_receivingException != null) {
//
//                    _socket.setSoTimeout(0);
//
//                        String exceptionMessage = _receivingException.getMessage();
//                        _receivingException = null;
//
//                        throw new ApiException(exceptionMessage);
//                    }


//                    lock.wait(_settings.getTimeout());

                    if (!waitTask(_settings.getTimeout())) {
                        if (lastErrorMsg != null) {
                            throw new ApiException(lastErrorMsg);
                        } else {
                            throw new ApiException("Terminal did not response within time out");
                        }
                    }

                    if (_terminalResponse != null) {

//                    _socket.setSoTimeout(0);

                        _isResponseNeeded = false;

                        return _terminalResponse;
                    }
                }
            }
        } catch (IOException | ApiException e) {
            throw new ApiException(e.getMessage());
        }

        return new byte[0];
    }

    @Override
    public void setMessageSentHandler(IMessageSentInterface messageInterface) {
        this.onMessageSent = messageInterface;
    }

    @Override
    public void setBroadcastMessageHandler(IBroadcastMessageInterface broadcastInterface) {
        this.onBroadcastMessage = broadcastInterface;
    }

    @Override
    public void setOnPayAtTableRequestHandler(IOnPayAtTableRequestInterface onPayAtTable) {
        this._onPayAtTableRequest = onPayAtTable;
    }

    //endregion


    private int tryParse(String value, int defaultVal) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
    private void initializeServer() throws ConfigurationException, IOException {
//        if (_serverSocket == null) {
//            if (_settings.getPort().isEmpty())
//                if (tryParse(_settings.getPort(), 0) == 0) {
//                    throw new ConfigurationException("Invalid port number.");
//                }
//
//            try {
//                int port = tryParse(_settings.getPort(), 0);
//                _serverSocket = new ServerSocket(port);
//                _socket = _serverSocket.accept();
//                _socket.setSoTimeout(_settings.getTimeout());
//                _out = new DataOutputStream(_socket.getOutputStream());
//                _in = new DataInputStream(_socket.getInputStream());
//                _receivingException = null;
//                _readData = true;
//                _isKeepAlive = false;
//            } catch (IOException e) {
//                throw new ConfigurationException(e.getMessage());
//            }
//        } else {
//            throw new ConfigurationException("Server already initialized.");
//        }

        try {
            if (!_settings.getPort().isEmpty()) {
                int port = Integer.parseInt(_settings.getPort());
                if (_serverSocket != null) {
                    _serverSocket.close();
                }

                // Start listening on set port.
                _serverSocket = new ServerSocket(port);
                _serverSocket.setSoTimeout(_settings.getTimeout());
                _readData = true;
                _receivingException = null;

                _isKeepAlive = false;
                lock = new Object();

            } else {
                throw new ConfigurationException("Port is missing.");
            }
        } catch (IOException e) {
            throw e;
        }
    }

    private void acceptClient() throws IOException {
        try {
            if (_serverSocket != null) {
                // Accept client here
                _socket = _serverSocket.accept();

                // Set timeout of data read

                // Get input and output stream from client.
                _out = new DataOutputStream(_socket.getOutputStream());
                _in = new DataInputStream(_socket.getInputStream());
            }
        } catch (IOException e) {
            throw e;
        }
    }

    private void analyzeReceivedData() throws ApiException {
//        try {
//            byte[] headerBuffer = new byte[2];
//
//            while (_readData) {
//                if (_settings.getConnectionMode() == ConnectionModes.PAY_AT_TABLE) {
//                    byte[] buffer = new byte[8192];
//                    _in.read(buffer, 0, buffer.length);
//
//                    MessageWriter byteArr = new MessageWriter();
//                    for (int i = 0; i < buffer.length; i++) {
//                        byteArr.add(buffer[i]);
//
//                        if (buffer[i] == ControlCodes.ETX.getByte()) {
//                            byteArr.add(buffer[i + 1]);
//                            break;
//                        }
//                    }
//
//                    Integer arrLen = byteArr.toArray().length;
//                    if (arrLen > 0) {
//                        String raw = TerminalUtilities.getString(byteArr.toArray());
//                        String dataETX = raw.substring(1, raw.length() - 2);
//
//                        String receivedLRC = raw.substring(raw.length() - 1);
//
//                        byte[] calculateLRC = TerminalUtilities.calculateLRC(dataETX);
//                        String calculatedLRC = new String(calculateLRC, StandardCharsets.ISO_8859_1);
//
//                        if (calculatedLRC.contentEquals(receivedLRC)) {
//                            String data = raw.substring(1, raw.length() - 2);
//
//                            PATRequest patRequest = new PATRequest(data.getBytes());
//                            if (_onPayAtTableRequest != null) {
//                                _onPayAtTableRequest.onPayAtTableRequest(patRequest);
//                            }
//                        }
//                    }
//                } else {
//                    int readHeader = _in.read(headerBuffer, 0, headerBuffer.length);
//
//                    if (!_readData) {
//                        break;
//                    }
//
//                    if (!_isKeepAlive && _isResponseNeeded) {
//                        _socket.setSoTimeout(_settings.getTimeout());
//                    }
//
//                    if (readHeader == -1) {
//                        _receivingException = new ApiException("Terminal disconnected");
//                    }
//
//                    int dataLength = TerminalUtilities.headerLength(headerBuffer);
//                    if (dataLength > 0) {
//                        byte[] dataBuffer = new byte[dataLength];
//
//                        boolean incomplete = true;
//                        int offset = 0;
//                        int tempLength = dataLength;
//
//                        do {
//                            int bytesReceived = _in.read(dataBuffer, offset, tempLength);
//
//                            if (!_readData) {
//                                break;
//                            }
//
//                            if (bytesReceived != tempLength) {
//                                offset += bytesReceived;
//                                tempLength -= bytesReceived;
//                            } else {
//                                incomplete = false;
//                            }
//                        } while (incomplete);
//
//                        byte[] readBuffer = new byte[dataLength];
//                        System.arraycopy(dataBuffer, 0, readBuffer, 0, dataLength);
//
//                        if (isBroadcast(readBuffer)) {
//                            BroadcastMessage broadcastMessage = new BroadcastMessage(readBuffer);
//                            if (onBroadcastMessage != null) {
//                                onBroadcastMessage.broadcastReceived(broadcastMessage.getCode(),
//                                        broadcastMessage.getMessage());
//                            }
//                        } else if (isKeepAlive(readBuffer) && new INGENICO_GLOBALS().KEEPALIVE) {
//                            _isKeepAlive = true;
//                            byte[] kResponse = keepAliveResponse(readBuffer);
//                            _out.write(kResponse);
//                            _out.flush();
//                        } else {
//                            _terminalResponse = readBuffer;
//                        }
//                    } else {
//                        _receivingException = new ApiException("No data received");
//                    }
//                }
//            }
//        } catch (Exception e) {
//            if (_isResponseNeeded || _isKeepAlive) {
//                _receivingException = new ApiException("Socket Error: " + e.getMessage());
//            }
//
//            if (_readData) {
//                analyzeReceivedData();
//            }
//        }


        byte[] headerBuffer = new byte[2];
        while (_readData) {
            try {
                if (_settings.getConnectionMode() == ConnectionModes.PAY_AT_TABLE) {
                    byte[] buffer = new byte[8192];
                    _in.read(buffer, 0, buffer.length);

                    MessageWriter byteArr = new MessageWriter();
                    for (int i = 0; i < buffer.length; i++) {
                        byteArr.add(buffer[i]);

                        if (buffer[i] == ControlCodes.ETX.getByte()) {
                            byteArr.add(buffer[i + 1]);
                            break;
                        }
                    }

                    Integer arrLen = byteArr.toArray().length;
                    if (arrLen > 0) {
                        String raw = TerminalUtilities.getString(byteArr.toArray());
                        String dataETX = raw.substring(1, raw.length() - 2);

                        String receivedLRC = raw.substring(raw.length() - 1);

                        byte[] calculateLRC = TerminalUtilities.calculateLRC(dataETX);
                        String calculatedLRC = new String(calculateLRC, StandardCharsets.UTF_8);

                        if (calculatedLRC.contentEquals(receivedLRC)) {
                            String data = raw.substring(1, raw.length() - 2);

                            PATRequest patRequest = new PATRequest(data.getBytes());
                            if (_onPayAtTableRequest != null) {
                                _onPayAtTableRequest.onPayAtTableRequest(patRequest);
                            }
                        }
                    }
                } else {
                    int readHeader = _in.read(headerBuffer, 0, headerBuffer.length);

                    if (!_readData) {
                        break;
                    }

//                    if (!_isKeepAlive && _isResponseNeeded) {
//                        _socket.setSoTimeout(_settings.getTimeout());
//                    }

                    if (readHeader == -1) {
                        _receivingException = new ApiException("Terminal disconnected");
                    }

                    int dataLength = TerminalUtilities.headerLength(headerBuffer);
                    if (dataLength > 0) {
                        byte[] dataBuffer = new byte[dataLength];

                        boolean incomplete = true;
                        int offset = 0;
                        int tempLength = dataLength;

                        do {
                            int bytesReceived = _in.read(dataBuffer, offset, tempLength);

                            if (!_readData) {
                                break;
                            }

                            if (bytesReceived != tempLength) {
                                offset += bytesReceived;
                                tempLength -= bytesReceived;
                            } else {
                                incomplete = false;
                            }
                        } while (incomplete);

                        byte[] readBuffer = new byte[dataLength];
                        System.arraycopy(dataBuffer, 0, readBuffer, 0, dataLength);
                        appendLog(new String(readBuffer, StandardCharsets.ISO_8859_1));
                        if (isBroadcast(readBuffer)) {
                            BroadcastMessage broadcastMessage = new BroadcastMessage(readBuffer);
                            if (onBroadcastMessage != null) {
                                onBroadcastMessage.broadcastReceived(broadcastMessage.getCode(),
                                        broadcastMessage.getMessage());
                            }
                        } else if (isKeepAlive(readBuffer) && new INGENICO_GLOBALS().KEEPALIVE) {
                            _isKeepAlive = true;
                            byte[] kResponse = keepAliveResponse(readBuffer);
                            _out.write(kResponse);
                            _out.flush();
                        } else {
                            _terminalResponse = readBuffer;
                            Log.i("RESPONSE:", String.valueOf(_terminalResponse.length));
                        }
                    } else {
                        _receivingException = new ApiException("No data received");
                    }
                }
            } catch (Exception e) {
                lastErrorMsg = e.getMessage();
//                if (_isResponseNeeded || _isKeepAlive) {
//                    _receivingException = new ApiException("Socket Error: " + e.getMessage());
//                }
//
//                if (_readData) {
//                    analyzeReceivedData();
//                }
            } finally {
                synchronized (lock) {
                    lock.notify();
                    isNotified = true;
                }
            }
        }


//        try {
//            while (_readData) {
//                if (_settings.getConnectionMode() == ConnectionModes.PAY_AT_TABLE) {
//                    byte[] buffer = new byte[8192];
//                    _in.read(buffer, 0, buffer.length);
//
//                    MessageWriter byteArr = new MessageWriter();
//                    for (int i = 0; i < buffer.length; i++) {
//                        byteArr.add(buffer[i]);
//
//                        if (buffer[i] == ControlCodes.ETX.getByte()) {
//                            byteArr.add(buffer[i + 1]);
//                            break;
//                        }
//                    }
//
//                    Integer arrLen = byteArr.toArray().length;
//                    if (arrLen > 0) {
//                        String raw = TerminalUtilities.getString(byteArr.toArray());
//                        String dataETX = raw.substring(1, raw.length() - 2);
//
//                        String receivedLRC = raw.substring(raw.length() - 1);
//
//                        byte[] calculateLRC = TerminalUtilities.calculateLRC(dataETX);
//                        String calculatedLRC = new String(calculateLRC, StandardCharsets.UTF_8);
//
//                        if (calculatedLRC.contentEquals(receivedLRC)) {
//                            String data = raw.substring(1, raw.length() - 2);
//
//                            PATRequest patRequest = new PATRequest(data.getBytes());
//                            if (_onPayAtTableRequest != null) {
//                                _onPayAtTableRequest.onPayAtTableRequest(patRequest);
//                            }
//                        }
//                    }
//                } else {
//                    int readHeader = _in.read(headerBuffer, 0, headerBuffer.length);
//
//                    if (!_readData) {
//                        break;
//                    }
//
//                    if (!_isKeepAlive && _isResponseNeeded) {
//                        _socket.setSoTimeout(_settings.getTimeout());
//                    }
//
//                    if (readHeader == -1) {
//                        _receivingException = new ApiException("Terminal disconnected");
//                    }
//
//                    int dataLength = TerminalUtilities.headerLength(headerBuffer);
//                    if (dataLength > 0) {
//                        byte[] dataBuffer = new byte[dataLength];
//
//                        boolean incomplete = true;
//                        int offset = 0;
//                        int tempLength = dataLength;
//
//                        do {
//                            int bytesReceived = _in.read(dataBuffer, offset, tempLength);
//
//                            if (!_readData) {
//                                break;
//                            }
//
//                            if (bytesReceived != tempLength) {
//                                offset += bytesReceived;
//                                tempLength -= bytesReceived;
//                            } else {
//                                incomplete = false;
//                            }
//                        } while (incomplete);
//
//                        byte[] readBuffer = new byte[dataLength];
//                        System.arraycopy(dataBuffer, 0, readBuffer, 0, dataLength);
//                        appendLog(new String(readBuffer, StandardCharsets.ISO_8859_1));
//                        if (isBroadcast(readBuffer)) {
//                            BroadcastMessage broadcastMessage = new BroadcastMessage(readBuffer);
//                            if (onBroadcastMessage != null) {
//                                onBroadcastMessage.broadcastReceived(broadcastMessage.getCode(),
//                                        broadcastMessage.getMessage());
//                            }
//                        } else if (isKeepAlive(readBuffer) && new INGENICO_GLOBALS().KEEPALIVE) {
//                            _isKeepAlive = true;
//                            byte[] kResponse = keepAliveResponse(readBuffer);
//                            _out.write(kResponse);
//                            _out.flush();
//                        } else {
//                            _terminalResponse = readBuffer;
//                            Log.i("RESPONSE:", String.valueOf(_terminalResponse.length));
//                        }
//                    } else {
//                        _receivingException = new ApiException("No data received");
//                    }
//                }
//            }
//        } catch (Exception e) {
//            if (_isResponseNeeded || _isKeepAlive) {
//                _receivingException = new ApiException("Socket Error: " + e.getMessage());
//            }
//
//            if (_readData) {
//                analyzeReceivedData();
//            }
//        }
    }

    private boolean isBroadcast(byte[] buffer) {
        return TerminalUtilities.getString(buffer).contains(new INGENICO_GLOBALS().BROADCAST);
    }

    private boolean isKeepAlive(byte[] buffer) {
        return TerminalUtilities.getString(buffer).contains(new INGENICO_GLOBALS().TID_CODE);
    }

    private byte[] keepAliveResponse(byte[] buffer) {
        if (buffer.length > 0) {
            int tidIndex = TerminalUtilities.getString(buffer).indexOf(new INGENICO_GLOBALS().TID_CODE);
            String terminalId = TerminalUtilities.getString(buffer);
            String response = String.format(new INGENICO_GLOBALS().KEEP_ALIVE_RESPONSE, terminalId);
            response = TerminalUtilities.calculateHeader(response.getBytes(UTF_8)) + response;

            return response.getBytes(UTF_8);
        } else
            return null;
    }

    private boolean waitTask(long timeout) {
        boolean result = true;
        isNotified = false;

        try {
            synchronized (lock) {
                lock.wait(timeout);

                if (!isNotified) {
                    result = false;
                }
            }
        } catch (Exception e) {
            lastErrorMsg = e.getMessage();
        }

        return result;
    }
}
