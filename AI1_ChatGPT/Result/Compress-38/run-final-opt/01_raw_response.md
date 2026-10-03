TARGETS: TarArchiveEntry header parsing used while reading PAX headers whose name ends in "/".
ORACLES: Trigger expectation: parsing succeeds (no IOException: "Error detected parsing the header").
CASES: Read an archive containing a PAX header with a trailing-slash name; continue to its entry.
CASES: Verify parsed entry name/type remains directory-compatible when name ends in "/".
RISKS: Available context omits header layout, PAX construction APIs, and precise expected entry fields.