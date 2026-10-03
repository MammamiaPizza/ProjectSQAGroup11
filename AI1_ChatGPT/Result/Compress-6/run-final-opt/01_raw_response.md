TARGETS: ZipArchiveEntry.equals(Object) and hashCode() consistency for entry state.  
ORACLES: Existing trigger ZipArchiveEntryTest::testNotEquals; equals contract and observable setters/getters.  
CASES: Same-name entries differing in relevant attributes should compare unequal.  
CASES: Equal independently created entries; self equality; comparison with null/non-ZipArchiveEntry.  
CASES: Boundary differences in method, internal/external attributes, platform, Unix mode, and extras.  
RISKS: Exact equality fields are unspecified here; avoid assuming behavior beyond trigger and public API.