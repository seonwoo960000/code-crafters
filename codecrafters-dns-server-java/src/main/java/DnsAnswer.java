import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DnsAnswer {

    private byte[] buffer;
    public final String name;
    public final DnsAnswerType type;
    public final DnsAnswerClass clazz;
    public final int ttlInSeconds;
    public final int rdlength;
    public final String rdata;

    private DnsAnswer(
            String name,
            DnsAnswerType type,
            DnsAnswerClass clazz,
            int ttlInSeconds,
            int rdlength,
            String rdata
    ) throws IOException {
        this.name = name;
        this.type = type;
        this.clazz = clazz;
        this.ttlInSeconds = ttlInSeconds;
        this.rdlength = rdlength;
        this.rdata = rdata;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String[] domainParts = name.split("\\.");
        for (String domainPart : domainParts) {
            outputStream.write((byte) domainPart.length());
            outputStream.write(domainPart.getBytes(StandardCharsets.UTF_8));
        }
        outputStream.write(Commons.NULL_VALUE);

        outputStream.write(ByteArrayUtils.createByteArray(2, type.getVal()));
        outputStream.write(ByteArrayUtils.createByteArray(2, clazz.getVal()));
        outputStream.write(ByteArrayUtils.createByteArray(4, ttlInSeconds));
        outputStream.write(ByteArrayUtils.createByteArray(2, rdlength));
        byte[] rdataInByteArray = ByteArrayUtils.createByteArrayFromIp(rdata);
        assert rdataInByteArray.length == rdlength;
        outputStream.write(rdataInByteArray);
        this.buffer = outputStream.toByteArray();
    }

    public static DnsAnswer of(
            String name,
            DnsAnswerType type,
            DnsAnswerClass clazz,
            int ttlInSeconds,
            int length,
            String data
    ) throws IOException {
        return new DnsAnswer(name, type, clazz, ttlInSeconds, length, data);
    }

    public static DnsAnswer of(byte[] buffer, int offset) throws IOException {
        int i = offset;
        List<String> domainParts = new ArrayList<>();
        while (i < buffer.length) {
            if (buffer[i] == Commons.NULL_VALUE) {
                i++;
                break;
            }

            int length = buffer[i++];
            char[] domainPart = new char[length];
            for (int j = 0; j < length; j++) {
                domainPart[j] = (char) buffer[i++];
            }

            domainParts.add(new String(domainPart));
        }
        String name = String.join(".", domainParts);
        DnsAnswerType type = DnsAnswerType.of(ByteArrayUtils.getIntFromByteArray(buffer, i, 2));
        DnsAnswerClass clazz = DnsAnswerClass.of(ByteArrayUtils.getIntFromByteArray(buffer, i + 2, 2));
        int ttlInSeconds = ByteArrayUtils.getIntFromByteArray(buffer, i + 4, 4);
        int rdlength = ByteArrayUtils.getIntFromByteArray(buffer, i + 8, 2);
        String rdata = ByteArrayUtils.getIpFromByteArray(buffer, i + 10, rdlength);
        return new DnsAnswer(name, type, clazz, ttlInSeconds, rdlength, rdata);
    }

    public byte[] getBuffer() {
        return ByteArrayUtils.copy(this.buffer);
    }

    @Override
    public String toString() {
        return "DnsAnswer{" +
               "name='" + name + '\'' +
               ", type=" + type +
               ", clazz=" + clazz +
               ", ttlInSeconds=" + ttlInSeconds +
               ", rdlength=" + rdlength +
               ", rdata='" + rdata + '\'' +
               '}';
    }
}

