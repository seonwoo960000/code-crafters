import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    private static final Map<Integer, ResolverAnswersHolder> headerIdToResolverAnswersHolder =
            new ConcurrentHashMap<>();
    private static final Map<Short, Integer> randomKeyToOriginalHeaderId = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new RuntimeException("No resolver address provided");
        }

        runConsideringResolver(args);
    }

    private static void runConsideringResolver(String[] args) {
        String ipAndPort = args[1];
        String[] ipAndPortParts = ipAndPort.split(":");
        InetSocketAddress resolverAddress = new InetSocketAddress(ipAndPortParts[0],
                                                                  Integer.parseInt(ipAndPortParts[1]));

        try (DatagramSocket serverSocket = new DatagramSocket(2053)) {
            while (true) {
                final byte[] buf = new byte[512];
                final DatagramPacket packet = new DatagramPacket(buf, buf.length);
                serverSocket.receive(packet);
                DnsMessage receivedMessage = DnsMessage.of(packet);
                System.out.println("Client address == " + packet.getSocketAddress());

                if (packet.getSocketAddress().equals(resolverAddress)) {
                    System.out.println("======Received from resolver======");
                    System.out.println(receivedMessage);
                    System.out.println("=================================");

                    short messageFromResolverId = (short) receivedMessage.header.getHeaderValue(DnsHeaderInfo.ID);
                    if (!randomKeyToOriginalHeaderId.containsKey(messageFromResolverId)) {
                        throw new RuntimeException("No question found for id: " + messageFromResolverId);
                    }

                    int originalHeaderId = randomKeyToOriginalHeaderId.get(messageFromResolverId);
                    randomKeyToOriginalHeaderId.remove(messageFromResolverId);

                    ResolverAnswersHolder holder = headerIdToResolverAnswersHolder.get(originalHeaderId);
                    holder.answersReceived(receivedMessage.answers); // only 1 answer will be provided
                    if (holder.isReadyToSendResponseToClient()) {
                        SocketAddress clientAddress = holder.originalMessage.targetAddress;
                        final DnsMessage message = setDefaultHeadersForClient(DnsMessage.of(
                                clientAddress,
                                holder.originalMessage.header,
                                holder.originalMessage.questions,
                                holder.answers
                        ));
                        System.out.println("======Sending to client======");
                        System.out.println(message);
                        System.out.println("=============================");
                        final DatagramPacket packetResponse = new DatagramPacket(message.getBuffer(),
                                                                                 message.getBuffer().length,
                                                                                 message.targetAddress);
                        serverSocket.send(packetResponse);
                        headerIdToResolverAnswersHolder.remove(originalHeaderId);
                    }
                } else {
                    System.out.println("======Received from client======");
                    System.out.println(receivedMessage);
                    System.out.println("================================");

                    int originalHeaderId = receivedMessage.header.id;
                    headerIdToResolverAnswersHolder.put(originalHeaderId,
                                                        new ResolverAnswersHolder(receivedMessage, new ArrayList<>()));

                    for (DnsQuestion question : receivedMessage.questions) {
                        DnsHeader header = DnsHeader.copy(receivedMessage.header);
                        short randomKey = getRandomKey();
                        header.setHeader(DnsHeaderInfo.ID, randomKey);
                        header.setHeader(DnsHeaderInfo.OPCODE, 0);
                        header.setHeader(DnsHeaderInfo.QDCOUNT, 1);
                        randomKeyToOriginalHeaderId.put(randomKey, originalHeaderId);
                        final DnsMessage message = setDefaultHeadersForResolver(DnsMessage.of(
                                resolverAddress,
                                header,
                                List.of(question),
                                List.of()));
                        System.out.println("======Sending to resolver======");
                        System.out.println(message);
                        System.out.println("================================");
                        final DatagramPacket packetResponse = new DatagramPacket(message.getBuffer(),
                                                                                 message.getBuffer().length,
                                                                                 resolverAddress);
                        serverSocket.send(packetResponse);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static DnsMessage wrapDnsMessageForTest(DnsMessage message) throws IOException {
        List<DnsQuestion> questions = message.questions.stream().map(
                question -> {
                    try {
                        return DnsQuestion.of(question.name, DnsQuestionType.A, DnsQuestionClass.IN);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
        Map<String, DnsAnswer> answerMap = new HashMap<>();
        for (DnsAnswer answer : message.answers) {
            answerMap.put(answer.name, answer);
        }
        List<DnsAnswer> answers = message.questions.stream().map(question -> {
            try {
                // for test only
                String data = answerMap.containsKey(question.name) ? answerMap.get(question.name).rdata :
                              "8.8.8.8";
                return DnsAnswer.of(question.name, DnsAnswerType.A, DnsAnswerClass.IN, 60, 4, data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        DnsHeader dnsHeader = DnsHeader.of(
                message.header.id,
                1,
                message.header.opcode,
                0,
                0,
                message.header.rd,
                0,
                0,
                message.header.opcode == 0 ? 0 : 4,
                questions.size(),
                answers.size(),
                0,
                0
        );
        return DnsMessage.of(message.targetAddress, dnsHeader, questions, answers);
    }

    private static DnsMessage setDefaultHeadersForClient(DnsMessage message) {
        DnsHeader header = message.header;
        List<DnsQuestion> questions = message.questions;
        List<DnsAnswer> answers = message.answers;

        header.setHeader(DnsHeaderInfo.QR, 1);
//        header.setHeader(DnsHeaderInfo.AA, 0);
//        header.setHeader(DnsHeaderInfo.TC, 0);
//        header.setHeader(DnsHeaderInfo.RA, 0);
//        header.setHeader(DnsHeaderInfo.Z, 0);
        header.setHeader(DnsHeaderInfo.RCODE, header.opcode == 0 ? 0 : 4);
        header.setHeader(DnsHeaderInfo.QDCOUNT, questions.size());
        header.setHeader(DnsHeaderInfo.ANCOUNT, answers.size());
//        header.setHeader(DnsHeaderInfo.NSCOUNT, 0);
//        header.setHeader(DnsHeaderInfo.ARCOUNT, 0);

        return message;
    }

    private static DnsMessage setDefaultHeadersForResolver(DnsMessage message) {
//        message.header.setHeader(DnsHeaderInfo.OPCODE, 1);
        return message;
    }

    private static short getRandomKey() {
        while (true) {
            Random random = new Random();
            short randomNumber = (short) random.nextInt(Short.MAX_VALUE + 1);
            if (!randomKeyToOriginalHeaderId.containsKey(randomNumber)) {
                return randomNumber;
            }
        }
    }
}