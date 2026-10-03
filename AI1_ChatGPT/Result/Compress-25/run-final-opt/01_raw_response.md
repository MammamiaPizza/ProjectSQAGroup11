TARGETS: ZipArchiveInputStream.getNextZipEntry(), read(byte[],int,int), and stored-entry reading.
ORACLES: Trigger expects first stored entry bytes unchanged; first byte expected 100, not 0.
CASES: Read first STORED entry after getNextZipEntry; assert returned bytes and payload equality.
CASES: Exercise partial reads and offset/length reads for first stored entry.
CASES: Boundary reads at entry EOF should return EOF without adding zero bytes.
RISKS: Context lacks archive fixture construction details and full stored-entry/data-descriptor semantics.