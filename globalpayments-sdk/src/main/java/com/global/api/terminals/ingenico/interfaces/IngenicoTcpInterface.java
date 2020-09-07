package com.global.api.terminals.ingenico.interfaces;

import com.global.api.entities.exceptions.ApiException;
import com.global.api.entities.exceptions.ConfigurationException;
import com.global.api.terminals.TerminalUtilities;
import com.global.api.terminals.abstractions.IDeviceCommInterface;
import com.global.api.terminals.abstractions.IDeviceMessage;
import com.global.api.terminals.abstractions.ITerminalConfiguration;
import com.global.api.terminals.ingenico.responses.BroadcastMessage;
import com.global.api.terminals.ingenico.variables.INGENICO_GLOBALS;
import com.global.api.terminals.messaging.IBroadcastMessageInterface;
import com.global.api.terminals.messaging.IMessageSentInterface;

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

public class IngenicoTcpInterface implements IDeviceCommInterface {
    private ServerSocket _serverSocket;
    private Socket _socket;
    private DataOutputStream _output;
    private InputStream _input;
    private ITerminalConfiguration _settings;
    private byte[] _terminalResponse;
    private Thread dataReceiving;
    private boolean _isKeepAlive;
    private Exception _receivingException;
    private BroadcastMessage _broadcastMessage;
    private boolean _isResponseNeeded = false;

    private IBroadcastMessageInterface onBroadcastMessage;
    private IMessageSentInterface onMessageSent;

    public IngenicoTcpInterface(ITerminalConfiguration settings) throws ConfigurationException {
        try {
            this._settings = settings;

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
            new Thread(new AnalyzeReceivedDataTask()).start();

        } catch (ConfigurationException | IOException e) {
            throw new ConfigurationException(e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        try {
            if (_serverSocket != null || !_serverSocket.isClosed()) {
                _input.close();
                _output.close();
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
            _output.write(buffer, 0, buffer.length);
            _output.flush();

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
                _output = new DataOutputStream(new BufferedOutputStream(
                        _socket.getOutputStream()
                ));
                _input = new DataInputStream(new BufferedInputStream(
                        _socket.getInputStream()
                ));
            }
        } catch (IOException e) {
            throw e;
        }
    }

    class AnalyzeReceivedDataTask implements Runnable {
        @Override
        public void run() {
            try {
                byte[] headerBuffer = new byte[2];
                while (_serverSocket != null || !_serverSocket.isClosed()) {

                    int readHeader = _input.read(headerBuffer, 0, headerBuffer.length);

                    if (!_isKeepAlive && _isResponseNeeded) {
                        _socket.setSoTimeout(_settings.getTimeout());
                    }

                    if (readHeader == -1) {
                        _receivingException = new ApiException("Error: Terminal disconnected");
//                        triggerSendBlock(new Exception("Error: Terminal disconnected"),7);
                        break;
                    }

                    int dataLength = TerminalUtilities.headerLength(headerBuffer);
                    byte[] tempBuffer = new byte[dataLength];

                    boolean incomplete = true;
                    int offset = 0;
                    int tempLength = dataLength;

                    do {
                        // Read data
                        int bytesReceived = _input.read(tempBuffer, offset, tempLength);
                        if (bytesReceived != tempLength) {
                            offset += bytesReceived;
                            tempLength -= bytesReceived;
                        } else {
                            incomplete = false;
                        }
                    } while (incomplete);

                    byte[] dataBuffer = new byte[dataLength];
                    System.arraycopy(tempBuffer, 0, dataBuffer, 0, dataLength);

                    if (isBroadcast(dataBuffer)) {
                        _broadcastMessage = new BroadcastMessage(dataBuffer);
                        onBroadcastMessage.broadcastReceived(_broadcastMessage.getCode(),
                                _broadcastMessage.getMessage());
                    } else if (isKeepAlive(dataBuffer) && new INGENICO_GLOBALS().KEEPALIVE) {

                        _isKeepAlive = true;

                        _socket.setSoTimeout(0);

                        byte[] kResponse = keepAliveResponse(dataBuffer);
                        _output.write(kResponse, 0, kResponse.length);
                        _output.flush();
                    } else {
                        _terminalResponse = dataBuffer;
                    }
                    headerBuffer = new byte[2];
                }
            } catch (InterruptedIOException e) {
                try {
                    if (_socket.getSoTimeout() > 0){
                        _receivingException = new ApiException("Error: Server Timeout");
                    } else {
                        run();
                    }
                } catch (SocketException ex) {
                    ex.printStackTrace();
                }
//                triggerSendBlock(e,5);
            } catch (Exception e) {
                if (_isResponseNeeded || _isKeepAlive) {
                    _receivingException = e;
                } else {
                    run();
                }
//                triggerSendBlock(e,6);
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
            response = TerminalUtilities.calculateHeader(response.getBytes(StandardCharsets.UTF_8)) + response;

            return response.getBytes(StandardCharsets.UTF_8);
        } else
            return null;
    }
}
