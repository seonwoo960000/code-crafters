import java.util.Arrays;

enum DnsQuestionClass {
    IN(1),
    CS(2),
    CH(3),
    HS(4);

    private final int val;

    private DnsQuestionClass(int val) {
        this.val = val;
    }

    public int getVal() {
        return this.val;
    }

    public static DnsQuestionClass of(int val) {
        return Arrays.stream(DnsQuestionClass.values()).filter(x -> x.getVal() == val).findFirst().orElse(null);
    }
}
