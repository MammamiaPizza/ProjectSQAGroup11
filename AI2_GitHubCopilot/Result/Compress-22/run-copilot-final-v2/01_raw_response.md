TARGETS: read(byte[],int,int) and read() on truncated bzip2 input
ORACLES: no IOException for truncated data; return -1 when stream exhausts available decompressed
bytes
CASES: (1) Read fully from truncated bzip2 → expect -1 after last byte
(2) Read partial then continue → -1 without IOException
(3) Empty stream → -1 on first read
(4) Header-only, no blocks → -1
(5) Truncated mid-block → -1 after some bytes
RISKS: exact expected behavior (IOException vs -1) depends on fixed code; need pre-made truncated
bzip2 payload; cannot generate compressed data without BZip2CompressorOutputStream