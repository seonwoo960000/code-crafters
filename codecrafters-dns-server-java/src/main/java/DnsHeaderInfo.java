enum DnsHeaderInfo {
    ID(0, 16),
    QR(16, 1),
    OPCODE(17, 4),
    AA(21, 1),
    TC(22, 1),
    RD(23, 1),
    RA(24, 1),
    Z(25, 3),
    RCODE(28, 4),
    QDCOUNT(32, 16),
    ANCOUNT(48, 16),
    NSCOUNT(64, 16),
    ARCOUNT(80, 16);

    private final int offset;
    private final int size;

    DnsHeaderInfo(int offset, int size) {
        this.offset = offset;
        this.size = size;
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }
}
