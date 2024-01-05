import java.util.Arrays;

enum DnsAnswerType {
    A(1),
    NS(2),
    MD(3),
    MF(4),
    CNAME(5),
    SOA(6),
    MB(7),
    MG(8),
    MR(9),
    NULL(10),
    WKS(11),
    PTR(12),
    HINFO(13),
    MINFO(14),
    MX(15),
    TXT(16);

    private final int val;

    DnsAnswerType(int val) {
        this.val = val;
    }

    public int getVal() {
        return this.val;
    }

    public static DnsAnswerType of(int val) {
        return Arrays.stream(DnsAnswerType.values()).filter(x -> x.getVal() == val).findFirst().orElse(null);
    }
}
