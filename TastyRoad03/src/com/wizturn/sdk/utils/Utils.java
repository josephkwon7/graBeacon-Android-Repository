package com.wizturn.sdk.utils;


public class Utils
{
  public static final char[] hexDigits = "0123456789abcdef".toCharArray();

  public static String normalizeProximityUUID(String proximityUUID) {
    String withoutDashes = proximityUUID.replace("-", "").toLowerCase();
    if (withoutDashes.length() != 32) {
      return "";
    }

    return String.format("%s-%s-%s-%s-%s", new Object[] { withoutDashes.substring(0, 8), withoutDashes.substring(8, 12), withoutDashes.substring(12, 16), withoutDashes.substring(16, 20), withoutDashes.substring(20, 32) });
  }

  public static int unsignedByteToInt(byte value) {
    return value & 0xFF;
  }

  public static String toString(byte[] scanRecord) {
    byte[] bytes = (byte[])scanRecord.clone();
    StringBuilder sb = new StringBuilder(2 * bytes.length);
    for (byte b : bytes) {
      sb.append(hexDigits[(b >> 4 & 0xF)]).append(hexDigits[(b & 0xF)]);
    }
    return sb.toString();
  }

  public static int normalize16BitUnsignedInt(int value) {
    return Math.max(1, Math.min(value, 65535));
  }

  public static double getDistance(float rssi, int measuredPower) {
    if (rssi == 0.0F) {
      return -1.0D;
    }

    double ratio = rssi / measuredPower;
    double rssiCorrection = 0.96D + Math.pow(Math.abs(rssi), 3.0D) % 10.0D / 150.0D;

    if (ratio <= 1.0D) {
      return Double.parseDouble(String.format("%.2f", new Object[] { Double.valueOf(Math.pow(ratio, 9.98D) * rssiCorrection) }));
    }
    return Double.parseDouble(String.format("%.2f", new Object[] { Double.valueOf((0.103D + 0.89978D * Math.pow(ratio, 7.71D)) * rssiCorrection) }));
  }

  public static byte[] fromString(String string) {
    byte[] bytes = new byte[string.length() / 2];
    for (int i = 0; i < string.length(); i += 2) {
      int ch1 = decode(string.charAt(i)) << 4;
      int ch2 = decode(string.charAt(i + 1));
      bytes[(i / 2)] = (byte)(ch1 + ch2);
    }
    return bytes;
  }

  private static int decode(char ch) {
    if ((ch >= '0') && (ch <= '9')) {
      return ch - '0';
    }
    if ((ch >= 'a') && (ch <= 'f')) {
      return ch - 'a' + 10;
    }
    throw new IllegalArgumentException("Illegal hexadecimal character: " + ch);
  }
}