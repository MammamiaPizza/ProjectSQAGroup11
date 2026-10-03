TARGETS: TarUtils.parseOctal, especially binary/base-256 values in TAR numeric header fields.
ORACLES: TarArchiveInputStreamTest::testCompress197 must parse its header without the reported error.
CASES: Valid ASCII octal fields; positive and negative base-256 values; smallest fitting field lengths.
CASES: Boundary values at octal/base-256 representation limits and leading padding/sign bytes.
CASES: Invalid numeric bytes should retain parseOctal's existing rejection behavior.
RISKS: No failing TAR fixture or exact expected parsed values are provided in this context.