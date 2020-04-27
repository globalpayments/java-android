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
import com.global.api.terminals.messaging.IMessageReceivedInterface;
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
import java.nio.charset.StandardCharsets;

public class IngenicoTcpInterface implements IDeviceCommInterface {
    private ServerSocket serverSocket;
    private Socket socket;
    private DataOutputStream output;
    private InputStream input;
    private ITerminalConfiguration settings;
    private byte[] terminalResponse;
    private Thread dataReceiving;
    private boolean isKeepAlive;
    private boolean isKeepAliveRunning;
    private Exception receivingException;
    private BroadcastMessage broadcastMessage;

    private IBroadcastMessageInterface onBroadcastMessage;
    private IMessageSentInterface onMessageSent;
    private IMessageReceivedInterface onMessageReceived;

    public IngenicoTcpInterface(ITerminalConfiguration settings) throws ConfigurationException {
        try {
            this.settings = settings;

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
            if (serverSocket != null || !serverSocket.isClosed()) {
                input.close();
                output.close();
                serverSocket.close();
            }
            serverSocket = null;
        } catch(IOException e) {
            // Eating the close exception
        }
    }

    @Override
    public byte[] send(IDeviceMessage message) throws ApiException {

        terminalResponse = null;
        receivingException = null;
        final byte[] buffer = message.getSendBuffer();

        try {
            if (serverSocket == null) {
                throw new ConfigurationException("Error: Server is not running.");
            }

            // Send request from builder.
            output.write(buffer, 0, buffer.length);
            output.flush();

            onMessageSent.messageSent(TerminalUtilities.getString(buffer).substring(2));

            while (terminalResponse == null) {
                Thread.sleep(10);

                if (receivingException != null) {
                    throw new ApiException(receivingException.getMessage());
                }

                if (terminalResponse != null) {
                    onMessageReceived.messageReceived(TerminalUtilities.getString(terminalResponse).substring(2));
                    return terminalResponse;
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
    public void setMessageReceivedHandler(IMessageReceivedInterface messageReceivedInterface) {
        this.onMessageReceived = messageReceivedInterface;
    }
    //endregion

    private void initializeServer() throws ConfigurationException, IOException {
        try {
            if (!settings.getPort().isEmpty()) {
                int port = Integer.parseInt(settings.getPort());
                if (serverSocket != null) {
                    serverSocket.close();
                }

                // Start listening on set port.
                serverSocket = new ServerSocket(port);
            } else {
                throw new ConfigurationException("Port is missing.");
            }
        } catch (IOException e) {
            throw e;
        }
    }

    private void acceptClient() throws IOException {
        try {
            if (serverSocket != null) {
                // Accept client here
                socket = serverSocket.accept();

                // Set timeout of data read
                socket.setSoTimeout(settings.getTimeout());

                // Get input and output stream from client.
                output = new DataOutputStream(new BufferedOutputStream(
                        socket.getOutputStream()
                ));
                input = new DataInputStream(new BufferedInputStream(
                        socket.getInputStream()
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
                while (serverSocket != null || !serverSocket.isClosed()) {

                    int readHeader = input.read(headerBuffer, 0, headerBuffer.length);

                    if (readHeader == -1) {
                        receivingException = new ApiException("Error: Terminal disconnected");
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
                        int bytesReceived = input.read(tempBuffer, offset, tempLength);
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
                        broadcastMessage = new BroadcastMessage(dataBuffer);
                        onBroadcastMessage.broadcastReceived(broadcastMessage.getCode(),
                                broadcastMessage.getMessage());
                    } else if (isKeepAlive(dataBuffer) && new INGENICO_GLOBALS().KEEPALIVE) {
                        byte[] kResponse = keepAliveResponse(dataBuffer);
                        output.write(kResponse, 0, kResponse.length);
                        output.flush();
                    } else {
                        terminalResponse = dataBuffer;
                    }
                    headerBuffer = new byte[2];
                }
            } catch (InterruptedIOException e){
                receivingException = e;
//                triggerSendBlock(e,5);
            } catch (Exception e) {
                receivingException = e;
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
