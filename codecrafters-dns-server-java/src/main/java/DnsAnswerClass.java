import java.util.Arrays;

enum DnsAnswerClass {
    IN(1),
    CS(2),
    CH(3),
    HS(4);

    private final int val;

    DnsAnswerClass(int val) {
        this.val = val;
    }

    public int getVal() {
        return this.val;
    }

    public static DnsAnswerClass of(int val) {
        return Arrays.stream(DnsAnswerClass.values()).filter(x -> x.getVal() == val).findFirst().orElse(null);
    }
}
