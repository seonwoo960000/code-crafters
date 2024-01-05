class DnsHeader {

    private byte[] buffer;
    public int id;
    public int qr;
    public int opcode;
    public int aa;
    public int tc;
    public int rd;
    public int ra;
    public int z;
    public int rcode;
    public int qdcount;
    public int ancount;
    public int nscount;
    public int arcount;

    private DnsHeader(
            int id,
            int qr,
            int opcode,
            int aa,
            int tc,
            int rd,
            int ra,
            int z,
            int rcode,
            int qdcount,
            int ancount,
            int nscount,
            int arcount
    ) {
        buffer = new byte[12];

        this.id = id;
        this.qr = qr;
        this.opcode = opcode;
        this.aa = aa;
        this.tc = tc;
        this.rd = rd;
        this.ra = ra;
        this.z = z;
        this.rcode = rcode;
        this.qdcount = qdcount;
        this.ancount = ancount;
        this.nscount = nscount;
        this.arcount = arcount;

        ByteArrayUtils.setByteArrayUsingBits(buffer, DnsHeaderInfo.ID.getOffset(), DnsHeaderInfo.ID.getSize(),
                                             id);
        ByteArrayUtils.setByteArrayUsingBits(buffer, DnsHeaderInfo.QR.getOffset(), DnsHeaderInfo.QR.getSize(),
                                             qr);
        ByteArrayUtils.setByteArrayUsingBits(buffer, DnsHeaderInfo.OPCODE.getOffset(),
                                             DnsHeaderInfo.OPCODE.getSize(),
                                             opcode);
        ByteArrayUtils.setByteArrayUsingBits(buffer, DnsHeaderInfo.AA.getOffset(), DnsHeaderInfo.AA.getSize(),
                                             aa);
        ByteArrayUtils.setByteArrayUsingBits(buffer, DnsHeaderInfo.TC.getOffset(), DnsHeaderInfo.TC.getSize(),
                                             tc);
        ByteArrayUtils.setByteArrayUsingBits(buffer, DnsHeaderInfo.RD.getOffset(), DnsHeaderInfo.RD.getSize(),
                                             rd);
        ByteArrayUtils.setByteArrayUsingBits(buffer, DnsHeaderInfo.RA.getOffset(), DnsHeaderInfo.RA.getSize(),
                                             ra);
        ByteArrayUtils.setByteArrayUsingBits(buffer, DnsHeaderInfo.Z.getOffset(), DnsHeaderInfo.Z.getSize(), z);
        ByteArrayUtils.setByteArrayUsingBits(buffer, DnsHeaderInfo.RCODE.getOffset(),
                                             DnsHeaderInfo.RCODE.getSize(),
                                             rcode);
        ByteArrayUtils.setByteArrayUsingBits(buffer, DnsHeaderInfo.QDCOUNT.getOffset(),
                                             DnsHeaderInfo.QDCOUNT.getSize(),
                                             qdcount);
        ByteArrayUtils.setByteArrayUsingBits(buffer, DnsHeaderInfo.ANCOUNT.getOffset(),
                                             DnsHeaderInfo.ANCOUNT.getSize(),
                                             ancount);
        ByteArrayUtils.setByteArrayUsingBits(buffer, DnsHeaderInfo.NSCOUNT.getOffset(),
                                             DnsHeaderInfo.NSCOUNT.getSize(),
                                             nscount);
        ByteArrayUtils.setByteArrayUsingBits(buffer, DnsHeaderInfo.ARCOUNT.getOffset(),
                                             DnsHeaderInfo.ARCOUNT.getSize(),
                                             arcount);
    }

    private DnsHeader(byte[] buffer) {
        this.buffer = ByteArrayUtils.copy(buffer);
        initializeHeaders();
    }

    public static DnsHeader of(byte[] buffer) {
        return new DnsHeader(buffer);
    }

    public int getHeaderValue(DnsHeaderInfo headerInfo) {
        return ByteArrayUtils.getIntFromByteArrayUsingBits(buffer, headerInfo.getOffset(),
                                                           headerInfo.getSize());
    }

    public void setHeader(DnsHeaderInfo headerInfo, int value) {
        ByteArrayUtils.setByteArrayUsingBits(buffer, headerInfo.getOffset(), headerInfo.getSize(), value);
        initializeHeaders();
    }

    private void initializeHeaders() {
        this.id = getHeaderValue(DnsHeaderInfo.ID);
        this.qr = getHeaderValue(DnsHeaderInfo.QR);
        this.opcode = getHeaderValue(DnsHeaderInfo.OPCODE);
        this.aa = getHeaderValue(DnsHeaderInfo.AA);
        this.tc = getHeaderValue(DnsHeaderInfo.TC);
        this.rd = getHeaderValue(DnsHeaderInfo.RD);
        this.ra = getHeaderValue(DnsHeaderInfo.RA);
        this.z = getHeaderValue(DnsHeaderInfo.Z);
        this.rcode = getHeaderValue(DnsHeaderInfo.RCODE);
        this.qdcount = getHeaderValue(DnsHeaderInfo.QDCOUNT);
        this.ancount = getHeaderValue(DnsHeaderInfo.ANCOUNT);
        this.nscount = getHeaderValue(DnsHeaderInfo.NSCOUNT);
        this.arcount = getHeaderValue(DnsHeaderInfo.ARCOUNT);
    }

    public static DnsHeader copy(DnsHeader header) {
        return new DnsHeader(
                header.getHeaderValue(DnsHeaderInfo.ID),
                header.getHeaderValue(DnsHeaderInfo.QR),
                header.getHeaderValue(DnsHeaderInfo.OPCODE),
                header.getHeaderValue(DnsHeaderInfo.AA),
                header.getHeaderValue(DnsHeaderInfo.TC),
                header.getHeaderValue(DnsHeaderInfo.RD),
                header.getHeaderValue(DnsHeaderInfo.RA),
                header.getHeaderValue(DnsHeaderInfo.Z),
                header.getHeaderValue(DnsHeaderInfo.RCODE),
                header.getHeaderValue(DnsHeaderInfo.QDCOUNT),
                header.getHeaderValue(DnsHeaderInfo.ANCOUNT),
                header.getHeaderValue(DnsHeaderInfo.NSCOUNT),
                header.getHeaderValue(DnsHeaderInfo.ARCOUNT)
        );
    }

    public static DnsHeader of(
            int id,
            int qr,
            int opcode,
            int aa,
            int tc,
            int rd,
            int ra,
            int z,
            int rcode,
            int qdcount,
            int ancount,
            int nscount,
            int arcount
    ) {
        return new DnsHeader(
                id,
                qr,
                opcode,
                aa,
                tc,
                rd,
                ra,
                z,
                rcode,
                qdcount,
                ancount,
                nscount,
                arcount
        );
    }

    public byte[] getBuffer() {
        return ByteArrayUtils.copy(this.buffer, 0, 12);
    }

    @Override
    public String toString() {
        return "DnsHeader{" +
               "id=" + id +
               ", qr=" + qr +
               ", opcode=" + opcode +
               ", aa=" + aa +
               ", tc=" + tc +
               ", rd=" + rd +
               ", ra=" + ra +
               ", z=" + z +
               ", rcode=" + rcode +
               ", qdcount=" + qdcount +
               ", ancount=" + ancount +
               ", nscount=" + nscount +
               ", arcount=" + arcount +
               '}';
    }
}

