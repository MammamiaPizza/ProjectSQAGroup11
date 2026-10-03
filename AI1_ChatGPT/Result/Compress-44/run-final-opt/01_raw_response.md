TARGETS: ChecksumCalculatingInputStream(Checksum, InputStream) null-argument validation.  
ORACLES: Trigger tests require NullPointerException when either constructor parameter is null.  
CASES: null checksum with valid InputStream; null InputStream with valid Checksum; both null.  
CASES: valid construction remains relevant; read/getValue behavior is not specified by provided failures.  
RISKS: No source body or existing-test details provided; avoid assuming NPE message or validation order.