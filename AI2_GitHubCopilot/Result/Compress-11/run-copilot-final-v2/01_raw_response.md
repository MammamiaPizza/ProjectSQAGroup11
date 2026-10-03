TARGETS: createArchiveInputStream(InputStream) — the only buggy method; must detect archive format
from stream
ORACLES: Should throw ArchiveException for non-archive input (e.g., short text "a"); for valid
archives return correct stream type
CASES: Valid: AR (!<arch>\n), CPIO (070707/070701), DUMP, JAR (ZIP+META-INF), TAR (ustar/gnu/pax),
ZIP (PK)
CASES: Invalid: short text, empty stream, null, truncated/partial archives, binary garbage
RISKS: TAR detection may lack minimum 512-byte block check; other detectors could also misclassify;
signature-only heuristics fragile
RISKS: Fix may break detection of valid small TAR entries; regression risk for other archive formats
in the factory chain