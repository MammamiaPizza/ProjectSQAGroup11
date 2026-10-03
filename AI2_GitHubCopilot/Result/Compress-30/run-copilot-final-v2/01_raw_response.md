TARGETS: read(byte[],int,int) when len==0 — returns -1 from EOF logic, must return 0 (InputStream
contract).
ORACLES: InputStream spec: “If len is zero, no bytes are read and 0 is returned.” Test expects 0
regardless of stream end.
CASES: len=0 with valid offs/dest, at EOF, after close?; len<0 → IndexOutOfBoundsException; positive
len works normally.
CASES: read() correctly returns -1 at EOF; verify len=0 shortcut doesn’t break block‑state
(initBlock, endBlock).
RISKS: Must check len==0 before any read0/complete logic; otherwise -1 propagates from end-of-stream
detection.
RISKS: Other CompressorInputStream implementations or delegation paths may inherit the same bug.