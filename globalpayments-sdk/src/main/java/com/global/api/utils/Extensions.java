package com.global.api.utils;

//import System;
//import System.Collections.Generic;
//import System.Net.Sockets;
//import System.Text.RegularExpressions;
//import System.Globalization;
//import System.Security.Cryptography;

import java.math.BigDecimal;
import java.util.Formatter;


public final class Extensions
{
    public static String FormatWith(String pattern, Object... values)
    {
        return new Formatter().format(pattern, values).toString();
    }

    public static String ToNumeric(String str)
    {
//			return Regex.Replace(str, "[^0-9]", ""); // rommel
        return null;
    }

    public static String ToNumericString(BigDecimal dec)
    {
//			return Regex.Replace(dec.toString(), "[^0-9]", ""); // rommel
        return null;
    }

    public static String ToNumericCurrencyString(BigDecimal dec)
    {
        if (dec != null)
        {
//				return Regex.Replace(String.format("{0:c}", dec), "[^0-9]", ""); // rommel
            return null;
        }
        return null;
    }

    public static String ToCurrencyString(BigDecimal dec)
    {
        if (dec != null)
        {
//              return Regex.Replace(String.FormatWith("{0:c}", dec), "[^0-9.,]", ""); // rommel
            return null;
        }
        return null;
    }


    public static BigDecimal ToAmount(String str)
    {
        if (str.isEmpty()|| str == null)
        {
            return null;
        }
        BigDecimal amount = new BigDecimal("0");
        amount = new BigDecimal(str);
        if (amount != null)
        {
            return amount.divide(new BigDecimal("100"));
        }
        return null;
    }

    public static<T> byte[] subArray(byte[] array, int index, int length) {
        System.arraycopy(array, index, array, 0, length);
        return array;
    }

    public static String ToInitialCase(Enum value)
    {
//            var initial = value.ToString();
//            return initial.SubString(0, 1).ToUpper() + initial.SubString(1).ToLower();
        String initial = value.toString();
        return initial.substring(0, 1).toUpperCase() + initial.substring(1).toLowerCase();

    }
    //		public static byte[] GetTerminalResponse(NetworkStream stream)    // rommel
    public static byte[] GetTerminalResponse(String stream)    // rommel
    {
//			byte[] buffer = new byte[4096];
//			int bytesReceived = stream.ReadAsync(buffer, 0, buffer.length).Result;
//			if (bytesReceived <= 0)
//			{
//				bytesReceived = stream.ReadAsync(buffer, 0, buffer.length).Result;
//			}
//
//			if (bytesReceived > 0)
//			{
//				byte[] readBuffer = new byte[bytesReceived];
//				System.arraycopy(buffer, 0, readBuffer, 0, bytesReceived);
//
//				ControlCodes code = ControlCodes.valueOf(readBuffer.toString()); // to do Java SE 10 rommel
//				if (code == ControlCodes.NAK)
//				{
//					return null;
//				}
//				else if (code == ControlCodes.EOT)
//				{
//					throw new MessageException("Terminal returned EOT for the current message.",null);
//				}
//				else if (code == ControlCodes.ACK)
//				{
//					return stream.GetTerminalResponse();
//				}
//				else if (code == ControlCodes.STX)
//				{
//					LinkedList<Byte> queue = new LinkedList<Byte>(readBuffer); // rommel
//
//					// break off only one message
//					ArrayList<Byte> rec_buffer = new ArrayList<Byte>();
//					do
//					{
//						rec_buffer.add(queue.poll());
//						if (rec_buffer[rec_buffer.size() - 1].equals((byte)ControlCodes.ETX))
//						{
//							break;
//						}
//					} while (!queue.isEmpty());
//
//					// Should be the LRC
//					if (!queue.isEmpty())
//					{
//						rec_buffer.add(queue.poll());
//					}
//					return tangible.ByteLists.toArray(rec_buffer); // rommel
//				}
//				else
//				{
//					throw new MessageException(String.format("Unknown message received: %1$s", code));
//				}
//			}
        return null;
    }

    //		public static byte[] GetTerminalResponseAsync(NetworkStream stream) // rommel
    public static byte[] GetTerminalResponseAsync(String stream)
    {
//			byte[] buffer = new byte[4096];
//			int bytesReceived = stream.ReadAsync(buffer, 0, buffer.length).Result;
//			if (bytesReceived > 0)
//			{
//				byte[] readBuffer = new byte[bytesReceived];
//				System.arraycopy(buffer, 0, readBuffer, 0, bytesReceived);
//
//				ControlCodes code = (ControlCodes)readBuffer[0];
//				if (code == ControlCodes.NAK)
//				{
//					return null;
//				}
//				else if (code == ControlCodes.EOT)
//				{
//					throw new MessageException("Terminal returned EOT for the current message.",null);
//				}
//				else if (code == ControlCodes.ACK)
//				{
//					return stream.GetTerminalResponse();
//				}
//				else if (code == ControlCodes.STX)
//				{
//					LinkedList<Byte> queue = new LinkedList<Byte>(readBuffer); //rommel
//					// break off only one message
//					ArrayList<Byte> rec_buffer = new ArrayList<Byte>(); //rommel
//					do
//					{
//						rec_buffer.add(queue.poll());
//						if (rec_buffer[rec_buffer.size() - 1].equals((byte)ControlCodes.ETX))
//						{
//							break;
//						}
//					} while (!queue.isEmpty());
//
//					// Should be the LRC
//					if (!queue.isEmpty())
//					{
//						rec_buffer.add(queue.poll());
//					}
//					return rec_buffer.toArray();//rommel
//				}
//				else
//				{
//					throw new MessageException(new Formatter().format("Unknown message received: %1$s", code).toString(),null);
//				}
//			}
        return null;
    }

    //		public static int ToInt32(String str)
//		{
//            if ((str.equals(null) || str.equals(" ")))
//			{
//				return 0;
//			}
//
//			int rvalue = 0;
////			tangible.OutObject<Integer> tempOut_rvalue = new tangible.OutObject<Integer>();
////			if (tangible.TryParseHelper.tryParseInt(str, tempOut_rvalue))
//			rvalue = Integer.parseInt(str);
//            if (rvalue != 0)
//			{
//				return rvalue;
//			}
//
//            return 0;
//		}
//    public static DateTime ToDateTime(String str) {
//			if (str.isEmpty()|| str == null) // rommel
//			{
//				return null;
//			}
//            DateTime rvalue;
//            if (DateTime.TryParseExact(str, "yyyyMMddhhmmss", CultureInfo.InvariantCulture, DateTimeStyles.None, out rvalue))
//                return rvalue;
//        return null;
//    }

    //        public static byte[] GetKey(Rfc2898DeriveBytes bytes) { // rommel
    public static byte[] GetKey(String bytes) {
//            return bytes.getBytes(32);
        return bytes.getBytes();
    }

    //        public static byte[] GetVector(this Rfc2898DeriveBytes bytes) { // rommel
    public static byte[] GetVector(String bytes) { // rommel
//            return bytes.GetBytes(16);
        return bytes.getBytes();
    }

// rommel
//        public static T GetValue<T>(this Dictionary<string, string> dict, string key)
//        {
//		static Dictionary<String, String> String; key) {
//            try {
//                return (T)Convert.ChangeType(dict[key], typeof(T));
//            }
//            catch (KeyNotFoundException) {
//                return default(T);
//            }
//        }
//        public static BigDecimal GetAmount(Dictionary<String, String> dict, String key) {
//            try {
//                return dict[key].ToAmount();
//            }
//            catch (KeyNotFoundException) {
//                return null;
//            }
//        }
//
//        public static boolean GetBoolean(Dictionary<String, String> dict, String key) {
//            try {
//                return boolean.TryParse(dict[key], out boolean result);
//            }
//            catch (KeyNotFoundException) {
//                return null;
//            }
//        }
//
//        public static IEnumerable<String> SplitInMaxDataSize(String str, int maxDataSize) {
//            if (String.IsNullOrEmpty(str)) {
//                yield return String.Empty;
//            }
//
//            for (var i = 0; i < str.Length; i += maxDataSize) {
//                yield return str.SubString(i, Math.Min(maxDataSize, str.Length - i));
//            }
//        }
//
//        public static String TrimEnd(String str, String trimString) {
//            String rvalue = str;
//            if (rvalue.endsWith(trimString)) {
//                int trimLength = trimString.length();
//                rvalue = rvalue.substring(0, rvalue.length() - trimLength);
//            }
//            return rvalue;
//        }
//        public static T[] SubArray<T>(T[] data, int index, int length) {
//            T[] result = new T[length];
//            Array.Copy(data, index, result, 0, length);
//            return result;
//        }
//
//        public static boolean IsNull<T>(this T data) {
//            return data == null;
//        }
}
