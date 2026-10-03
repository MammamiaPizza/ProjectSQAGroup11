TARGETS: ArArchiveInputStream.getNextArEntry, getNextEntry, close, read(byte[],int,int)
ORACLES: AR format spec; ArArchiveEntry name/size; expected entry count from testArDelete (1); null
on EOF
CASES: zero-length entry; single entry; multiple entries; empty stream; read after close; partial
buffers
RISKS: no test archive data; exact AR padding rules unknown; testArDelete may rely on specific
deleted-entry archive