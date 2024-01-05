import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

class DnsQuestion {

    private final byte[] buffer;
    public final String name;
    public final DnsQuestionType type;
    public final DnsQuestionClass clazz;

    private DnsQuestion(String name, DnsQuestionType type, DnsQuestionClass clazz) throws IOException {
        this.name = name;
        this.type = type;
        this.clazz = clazz;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String[] domainParts = name.split("\\.");
        for (String domainPart : domainParts) {
            outputStream.write((byte) domainPart.length());
            outputStream.write(domainPart.getBytes(StandardCharsets.UTF_8));
        }
        outputStream.write(Commons.NULL_VALUE);

        outputStream.write(ByteArrayUtils.createByteArray(2, type.getVal()));
        outputStream.write(ByteArrayUtils.createByteArray(2, clazz.getVal()));
        this.buffer = outputStream.toByteArray();
    }

    public static DnsQuestion of(String domain, DnsQuestionType type, DnsQuestionClass clazz)
            throws IOException {
        return new DnsQuestion(domain, type, clazz);
    }

    public static DnsQuestion of(byte[] buffer, int offset) throws IOException {
        int[] lastOffsetHolder = new int[] { offset };
        String domain = uncompress(buffer, offset, lastOffsetHolder);
        int lastOffset = lastOffsetHolder[0];
        DnsQuestionType type = DnsQuestionType.of(ByteArrayUtils.getIntFromByteArray(buffer, lastOffset, 2));
        DnsQuestionClass clazz = DnsQuestionClass.of(
                ByteArrayUtils.getIntFromByteArray(buffer, lastOffset + 2, 2));
        return new DnsQuestion(domain, type, clazz);
    }

    private static String uncompress(byte[] buffer, int offset, int[] lastOffsetHolder) {
//        ByteArrayUtils.debug(buffer, offset, 2);
        if (buffer[offset] == Commons.NULL_VALUE) {
            lastOffsetHolder[0] = ++offset;
            return null;
        }

        if (isCompressedMessage(buffer, offset)) {
            int pointerOffset = ByteArrayUtils.getIntFromByteArrayUsingBits(buffer, 8 * offset + 2, 14);
            return uncompress(buffer, pointerOffset, lastOffsetHolder);
        }

        int i = offset;
        int length = buffer[i++];
        char[] domainPart = new char[length];
        for (int j = 0; j < length; j++) {
            domainPart[j] = (char) buffer[i++];
        }

        String next = uncompress(buffer, i, lastOffsetHolder);
        return next != null ? new String(domainPart) + "." + next : new String(domainPart);
    }

    private static boolean isCompressedMessage(byte[] buffer, int offset) {
        return ByteArrayUtils.isBitSet(buffer, offset * 8) &&
               ByteArrayUtils.isBitSet(buffer, offset * 8 + 1);
    }

    public byte[] getBuffer() {
        return ByteArrayUtils.copy(this.buffer);
    }

    @Override
    public String toString() {
        return "DnsQuestion{" +
               "domain='" + name + '\'' +
               ", type=" + type +
               ", clazz=" + clazz +
               '}';
    }
}

