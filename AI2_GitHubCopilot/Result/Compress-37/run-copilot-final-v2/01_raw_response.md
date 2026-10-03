TARGETS: getNextTarEntry() -> paxHeaders() -> parsePaxHeaders(InputStream) internal parsing of
extended PAX headers from tar records
ORACLES: blank/empty lines must not throw NegativeArraySizeException; they are skipped silently;
remaining key=value pairs still parsed correctly
ORACLES: getNextTarEntry() returns a TarArchiveEntry with expected name/size/attributes after
blank-line handling
CASES: single blank line between valid PAX keywords; multiple consecutive blank lines; blank line at
start/end of PAX data; only blank lines (empty extended header)
CASES: line consisting only of spaces/tabs (treated as blank); blank line with trailing carriage
return; blank line after record padding but another record follows
RISKS: parsePaxHeaders is private, testable only indirectly via crafted TarArchiveInputStream; hard
to construct tar bytes manually; must preserve tar blocking