TARGETS: TarUtils.parseOctal(byte[], int, int), especially validation of octal-field bytes.  
TARGETS: TarUtils parsing behavior as reached by TarTestCase::testCOMPRESS178.  
ORACLES: Trigger tests specify IllegalArgumentException for parseOctal with embedded NUL.  
ORACLES: Trigger test specifies IOException for COMPRESS178 archive processing.  
CASES: Valid octal fields with leading/trailing padding and representative numeric values.  
CASES: Embedded NUL within an otherwise populated octal field; expect IllegalArgumentException.  
CASES: Invalid non-octal bytes and boundary offsets/lengths within supplied buffers.  
RISKS: COMPRESS178's exact malformed archive input and TarUtils call path are not provided.  
RISKS: Do not infer exception messages or unlisted behavior from another program version.