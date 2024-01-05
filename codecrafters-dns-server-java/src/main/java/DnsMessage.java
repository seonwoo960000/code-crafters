import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

class DnsMessage {
    public final SocketAddress targetAddress;
    public final DnsHeader header;
    public final List<DnsQuestion> questions;
    public final List<DnsAnswer> answers;

    private DnsMessage(SocketAddress targetAddress, DnsHeader header, List<DnsQuestion> questions,
                       List<DnsAnswer> answers) {
        this.targetAddress = targetAddress;
        this.header = header;
        this.questions = questions;
        this.answers = answers;
    }

    public static DnsMessage of(SocketAddress targetAddress,
                                DnsHeader header,
                                List<DnsQuestion> questions,
                                List<DnsAnswer> answers) {
        return new DnsMessage(targetAddress, header, questions, answers);
    }

    public static DnsMessage of(DatagramPacket packet) throws IOException {
        byte[] buffer = packet.getData();
        DnsHeader header = DnsHeader.of(buffer);
        List<DnsQuestion> questions = new ArrayList<>();
        List<DnsAnswer> answers = new ArrayList<>();

        int startIdx = header.getBuffer().length;
        for (int i = 0; i < header.qdcount; i++) {
            DnsQuestion question = DnsQuestion.of(buffer, startIdx);
//            System.out.println("================");
//            System.out.println(question);
//            System.out.println("================");
            questions.add(question);
            startIdx += question.getBuffer().length;
        }

        for (int i = 0; i < header.ancount; i++) {
            DnsAnswer answer = DnsAnswer.of(buffer, startIdx);
//            System.out.println("================");
//            System.out.println(answer);
//            System.out.println("================");
            answers.add(answer);
            startIdx += answer.getBuffer().length;
        }

        return new DnsMessage(packet.getSocketAddress(), header, questions, answers);
    }

    public byte[] getBuffer() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(this.header.getBuffer());
        for (DnsQuestion question : questions) {
            outputStream.write(question.getBuffer());
        }
        for (DnsAnswer answer : answers) {
            outputStream.write(answer.getBuffer());
        }
        return outputStream.toByteArray();
    }

    @Override
    public String toString() {
        return "DnsMessage{" +
               "targetAddress=" + targetAddress +
               ", header=" + header +
               ", questions=" + questions +
               ", answers=" + answers +
               '}';
    }
}
