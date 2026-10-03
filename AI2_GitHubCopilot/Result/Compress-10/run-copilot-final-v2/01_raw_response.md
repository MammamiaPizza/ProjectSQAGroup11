TARGETS: ZipFile.getInputStream, getEntry, getEntries, populateFromCentralDirectory,
GeneralPurposeBit.usesUTF8ForNames, positionAtCentralDirectory
ORACLES: getInputStream must return non-null for entries with UTF-8 name/comment; canReadEntryData
must return true
ORACLES: InputStream must decompress and read entry content correctly (e.g., match known bytes)
CASES: Normal: entry with umlaut in name from WinZip archive; entry with both UTF-8 flag and Unicode
Extra Field
CASES: Boundary: entry at end of central directory; entry with empty comment; ASCII-only name entry
CASES: Error: entry missing local file header; entry with mismatched encoding flag vs actual name
encoding
RISKS: Original WinZip test archive content unknown; bug likely in offset lookup for UTF-8 entries;
relies on archive fixture