TARGETS: Java Date setters/getters and dateToZipLong/unixTimeToZipLong conversion boundaries.  
ORACLES: Existing getter/setter trigger; Date↔ZipLong results and flags exposed by public getters.  
CASES: Set modify/access/create Java times within unsigned 32-bit Unix-time range; verify round-trip.  
CASES: Test Unix epoch and maximum 32-bit unsigned seconds via Date setters without assertion failure.  
CASES: Verify each setter marks only its corresponding presence flag and retains supplied ZipLong.  
RISKS: Date precision is milliseconds while ZIP timestamps are seconds; expected values may be truncated.  
RISKS: No source/body details or valid out-of-range Date behavior are provided.