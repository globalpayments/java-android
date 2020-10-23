package com.global.api.terminals.ingenico.interfaces;

import android.util.Log;

import com.global.api.entities.enums.ConnectionModes;
import com.global.api.entities.enums.ControlCodes;
import com.global.api.entities.exceptions.ApiException;
import com.global.api.entities.exceptions.ConfigurationException;
import com.global.api.terminals.TerminalUtilities;
import com.global.api.terminals.abstractions.IDeviceCommInterface;
import com.global.api.terminals.abstractions.IDeviceMessage;
import com.global.api.terminals.abstractions.ITerminalConfiguration;
import com.global.api.terminals.ingenico.pat.PATRequest;
import com.global.api.terminals.ingenico.responses.BroadcastMessage;
import com.global.api.terminals.ingenico.variables.DeviceMode;
import com.global.api.terminals.ingenico.variables.INGENICO_GLOBALS;
import com.global.api.terminals.messaging.IBroadcastMessageInterface;
import com.global.api.terminals.messaging.IMessageSentInterface;
import com.global.api.terminals.messaging.IOnPayAtTableRequestInterface;
import com.global.api.utils.MessageWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

import static com.global.api.terminals.TerminalUtilities.calculateLRC;
import static java.nio.charset.StandardCharsets.*;

public class IngenicoTcpInterface implements IDeviceCommInterface {
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
            new Thread(new Runnable() {
                public void run() {
                    receviedData();
                }
            }).start();

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
            }
            _serverSocket = null;
        } catch (IOException e) {
            // Eating the close exception
        }
    }

    @Override
    public byte[] send(IDeviceMessage message) throws ApiException {

        _terminalResponse = null;
        _isResponseNeeded = true;

        final byte[] buffer = message.getSendBuffer();

        try {
            if (_serverSocket == null) {
                throw new ConfigurationException("Error: Server is not running.");
            }

            // Send request from builder.
            _socket.setSoTimeout(_settings.getTimeout());
            _out.write(buffer, 0, buffer.length);
            _out.flush();

            onMessageSent.messageSent(TerminalUtilities.getString(buffer).substring(2));

            while (_terminalResponse == null) {
                Thread.sleep(10);

                if (_receivingException != null) {

                    _socket.setSoTimeout(0);

                    String exceptionMessage = _receivingException.getMessage();
                    _receivingException = null;

                    throw new ApiException(exceptionMessage);
                }

                if (_terminalResponse != null) {

                    _socket.setSoTimeout(0);

                    _isResponseNeeded = false;
                    return _terminalResponse;
                }
            }
        } catch (InterruptedException | IOException | ApiException e) {
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

    private void initializeServer() throws ConfigurationException, IOException {
        try {
            if (!_settings.getPort().isEmpty()) {
                int port = Integer.parseInt(_settings.getPort());
                if (_serverSocket != null) {
                    _serverSocket.close();
                }

                // Start listening on set port.
                _serverSocket = new ServerSocket(port);
                _readData = true;
                _receivingException = null;

                _isKeepAlive = false;

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

    private void receviedData() {
        try {
            byte[] headerBuffer = new byte[2];
            while (_readData) {
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
                        String calculatedLRC = new String(calculateLRC, UTF_8);

                        if (calculatedLRC.contentEquals(receivedLRC)) {
                            String data = dataETX;

                            PATRequest patRequest = new PATRequest(data.getBytes());
                            if (_onPayAtTableRequest != null) {
                                _onPayAtTableRequest.onPayAtTableRequest(patRequest);
                            }
                        }
                    }
                } else {
                    int dataLength = TerminalUtilities.headerLength(headerBuffer);
                    byte[] dataBuffer = new byte[dataLength + 2];

                    Thread.sleep(1000);
                    _in.read(dataBuffer, 0, dataBuffer.length);

                    if (!_readData) {
                        break;
                    }

                    if (_receivingException != null) {
                        dataBuffer = null;
                    }

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
                    }
                }
            }
        } catch (Exception e) {
            if (_isResponseNeeded || _isKeepAlive) {
                _receivingException = new ApiException("Socket Error: " + e.getMessage());
            }
        }
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
}
