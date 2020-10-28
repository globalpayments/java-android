package com.global.api.terminals;

import android.graphics.Point;

import com.global.api.entities.enums.ConnectionModes;
import com.global.api.entities.enums.ControlCodes;
import com.global.api.entities.enums.IByteConstant;
import com.global.api.entities.enums.IStringConstant;
import com.global.api.entities.exceptions.BuilderException;
import com.global.api.terminals.abstractions.IRequestSubGroup;
import com.global.api.utils.Extensions;
import com.global.api.utils.MessageWriter;

import java.nio.charset.StandardCharsets;

public class TerminalUtilities {
    private static final String version = "1.35";

    public static String getElementString(Object... elements) {
        StringBuilder sb = new StringBuilder();
        for (Object element : elements) {
            if (element instanceof ControlCodes) {
                sb.append((char) ((ControlCodes) element).getByte());
            } else if (element instanceof IRequestSubGroup) {
                sb.append(((IRequestSubGroup) element).getElementString());
            } else if (element instanceof String[]) {
                for (String sub_element : (String[]) element) {
                    sb.append(ControlCodes.FS.getByte());
                    sb.append(sub_element);
                }
            } else if (element instanceof IStringConstant) {
                sb.append(((IStringConstant) element).getValue());
            } else if (element instanceof IByteConstant) {
                sb.append(((IByteConstant) element).getByte());
            } else
                sb.append(element);
        }

        return sb.toString();
    }

    public static String getString(byte[] buffer) {
        return new String(buffer, StandardCharsets.UTF_8);
    }

    public static Integer headerLength(byte[] buffer) {
        String fHex = String.format("%02X", buffer[0]);
        String sHex = String.format("%02X", (buffer[1] & 255));
        String hex = fHex + sHex;
        return Integer.parseInt(hex, 16);
    }

    public static String calculateHeader(byte[] buffer) {
        String hex = String.format("%04x", buffer.length);
        String fDigit = Character.toString(hex.charAt(0)) + hex.charAt(1);
        String sDigit = Character.toString(hex.charAt(2)) + hex.charAt(3);

        return String.format("%c%c", (char) Extensions.parseUnsignedInt(fDigit, 16), (char) Extensions.parseUnsignedInt(sDigit, 16));

    }

    public static DeviceMessage buildIngenicoRequest(String message, ConnectionModes settings) throws BuilderException {
        MessageWriter buffer = new MessageWriter();
        byte lrc;

        switch (settings) {
            case SERIAL:
                throw new BuilderException("Failed to build request message. Not available for this library.");
            case PAY_AT_TABLE:
                buffer.add(ControlCodes.STX.getByte());
                for (char c : message.toCharArray())
                    buffer.add((byte) c);
                buffer.add(ControlCodes.ETX.getByte());
                byte[] arrByte = calculateLRC(message);
                lrc = arrByte[0];
                buffer.add(lrc);

                break;
            case TCP_IP_SERVER:
                String msg = calculateHeader(message.getBytes(StandardCharsets.UTF_8)) + message;
                for (char c : msg.toCharArray())
                    buffer.add((byte) c);
                break;
            default:
                throw new BuilderException("Failed to build request message. Unknown Connection mode.");
        }

        return new DeviceMessage(buffer.toArray());
    }

    public static byte[] calculateLRC(String requestMessage) {
        byte[] cCOde = new byte[]{ControlCodes.ETX.getByte()};
        int index1 = requestMessage.getBytes().length;
        int index2 = cCOde.length;
        byte[] bytes = new byte[index1 + index2];
        System.arraycopy(requestMessage.getBytes(), 0, bytes, 0, index1);
        System.arraycopy(cCOde, 0, bytes, index1, index2);

        byte lrc = 0;
        for (int i = 0; i < bytes.length; i++) {
            lrc ^= bytes[i];
        }
        bytes = new byte[]{lrc};
        return bytes;

    }

    private static Point toPoint(String coordinate) {
        String[] xy = coordinate.split("\\[COMMA]");

        Point rvalue = new Point();
        rvalue.x = Integer.parseInt(xy[0]);
        rvalue.y = Integer.parseInt(xy[1]);

        return rvalue;
    }
}
