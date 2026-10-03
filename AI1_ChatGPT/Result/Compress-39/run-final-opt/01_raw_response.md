TARGETS: ArchiveUtils.sanitize(String): replaces ISO control characters and enforces its output-length limit.  
ORACLES: Trigger sanitizeShortensString expects long input to be shortened, not returned intact.  
CASES: Normal printable string remains unchanged; ISO control characters are replaced with '?'.  
CASES: Boundary strings at and around the supported maximum length; over-limit input is truncated.  
CASES: Long patterned input verifies retained prefix and omitted suffix.  
RISKS: Exact maximum length is not stated in the provided context; derive assertions from available test/source only.