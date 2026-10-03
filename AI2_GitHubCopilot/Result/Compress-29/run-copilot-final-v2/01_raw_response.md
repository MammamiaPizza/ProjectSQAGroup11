TARGETS: ArchiveStreamFactory(String), setEntryEncoding/getEntryEncoding,
CpioArchiveInputStream(InputStream,String), CpioArchiveOutputStream(OutputStream,String),
TarArchiveInputStream(InputStream,String), TarArchiveOutputStream(OutputStream,String),
ZipArchiveInputStream(InputStream,String)
ORACLES: verify getEntryEncoding() returns factory/constructor encoding; entry names decoded with
that encoding; tests count failures per archive format
CASES: factory with null/empty/unsupported encoding; constructors with encoding vs default; reading
entries with non-ASCII names using specified encoding; boundary: platform default fallback
RISKS: only known triggers; DumpArchiveInputStream encoding constructor not visible; some formats
may ignore encoding; no source to verify encoding propagation