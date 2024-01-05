public class ByteArrayUtils {
    public static void setByteArrayUsingBits(byte[] src, int bitOffset, int bitLength, int value) {
        for (int i = 0; i < bitLength; i++) {
            int offsetInByte = (bitOffset + i) / 8;
            int offsetInByteRemainder = (bitOffset + i) % 8;

            int bitValue = (value & (1 << (bitLength - 1 - i)));
            if (bitValue == 0) {
                src[offsetInByte] &= (byte) ~(1 << (7 - offsetInByteRemainder));
            } else {
                src[offsetInByte] |= (byte) (1 << (7 - offsetInByteRemainder));
            }
        }
    }

    public static int getIntFromByteArrayUsingBits(byte[] src, int bitOffset, int bitLength) {
        int result = 0;
        for (int i = 0; i < bitLength; i++) {
            int offsetInByte = (bitOffset + i) / 8;
            int offsetInByteRemainder = (bitOffset + i) % 8;

            if ((src[offsetInByte] & (1 << (7 - offsetInByteRemainder))) == 0) {continue;}
            result |= (1 << (bitLength - 1 - i));
        }
        return result;
    }

    public static int getIntFromByteArray(byte[] src, int offset, int length) {
        int result = 0;
        for (int i = 0; i < length; i++) {
            result |= (src[offset + i] & 0xFF) << (8 * (length - 1 - i));
        }
        return result;
    }

    public static byte[] createByteArray(int size, int value) {
        int sizeInBit = size * 8;
        byte[] result = new byte[size];

        for (int i = 0; i < sizeInBit; i++) {
            int offsetInByte = (i) / 8;
            int offsetInByteRemainder = (i) % 8;

            if ((value & (1 << (sizeInBit - 1 - i))) == 0) {continue;}
            result[offsetInByte] |= (byte) (1 << (7 - offsetInByteRemainder));
        }

        return result;
    }

    public static byte[] createByteArrayFromIp(String ip) {
        String[] ipParts = ip.split("\\.");
        byte[] result = new byte[ipParts.length];
        for (int i = 0; i < ipParts.length; i++) {
            result[i] = (byte) Integer.parseInt(ipParts[i]);
        }
        return result;
    }

    public static String getIpFromByteArray(byte[] src, int offset, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(src[offset + i] & 0xFF);
            if (i != src.length - 1) {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    public static boolean isBitSet(byte[] src, int offsetInBit) {
        return getIntFromByteArrayUsingBits(src, offsetInBit, 1) == 1;
    }

    public static byte[] copy(byte[] src) {
        byte[] dst = new byte[src.length];
        System.arraycopy(src, 0, dst, 0, src.length);
        return dst;
    }

    public static byte[] copy(byte[] src, int start, int end) {
        byte[] dst = new byte[end - start];
        System.arraycopy(src, start, dst, 0, end - start);
        return dst;
    }

    public static byte[] copy(byte[] src, int offset) {
        byte[] dst = new byte[src.length - offset];
        System.arraycopy(src, offset, dst, 0, src.length - offset);
        return dst;
    }

    public static void debug(byte[] src) {
        for (byte b : src) {
            String binaryString = Integer.toBinaryString(b & 0xFF);
            String paddedBinaryString = String.format("%8s", binaryString).replace(' ', '0');
            System.out.println(paddedBinaryString);
        }
    }

    public static void debug(byte[] src, int offset, int length) {
        int max = Math.min((offset + length), src.length);
        for (int i = offset; i < max; i++) {
            System.out.printf("%8s", Integer.toBinaryString(src[i] & 0xFF)).println();
        }
    }

    public static void debugHex(byte[] src) {
        for (int i = 0; i < src.length; i++) {
            System.out.printf("%02X ", src[i]);
        }
        System.out.println();
    }
}
