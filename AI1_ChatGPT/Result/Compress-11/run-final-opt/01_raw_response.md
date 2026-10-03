TARGETS: ArchiveStreamFactory.createArchiveInputStream(InputStream) auto-detection, especially TAR recognition.
ORACLES: Trigger expects short text/non-archive input not to yield an ArchiveInputStream.
CASES: Short textual streams below TAR header size; verify rejection rather than TAR stream creation.
CASES: Valid archive signatures/formats only if existing tests/resources establish expected detection.
RISKS: Detection may read/reset input; use mark-supported stream as required by existing API behavior.
RISKS: Context provides only one overload and no exception type or TAR signature details; do not assume them.