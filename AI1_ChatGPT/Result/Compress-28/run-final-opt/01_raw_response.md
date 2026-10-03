TARGETS: TarArchiveInputStream.read(byte[],int,int) while reading current TAR entry data
TARGETS: getNextTarEntry() when advancing past an unread or truncated current entry
ORACLES: Trigger test shouldThrowAnExceptionOnTruncatedEntries expects IOException
CASES: Valid entry: read returns available entry bytes and then -1 at declared entry end
CASES: Truncated entry body: read request reaching missing declared bytes throws IOException
CASES: Truncated entry followed by getNextTarEntry(): advancing detects truncation and throws IOException
CASES: Boundary reads at exact entry size and one byte beyond declared size
RISKS: InputStream may return short reads; distinguish valid short reads from premature physical EOF
RISKS: Context lacks TAR fixture layout and exact IOException message/type requirements