TARGETS: DataUtil.load(InputStream,...), parseByteData(ByteBuffer,...), BOM handling before HTML parsing
ORACLES: Trigger expects parsed body text "One" after discarding a spurious byte-order mark
CASES: UTF-8 BOM-prefixed input with unspecified charset; verify document text is retained, not empty
CASES: BOM-prefixed input with explicit charset; verify BOM does not become content or prevent parsing
CASES: Normal non-BOM input; verify existing parsing path remains functional
RISKS: parseByteData is package-private; tests may need same-package access or exercise public load
RISKS: No source/test fixture details supplied; derive assertions only from trigger and available signatures