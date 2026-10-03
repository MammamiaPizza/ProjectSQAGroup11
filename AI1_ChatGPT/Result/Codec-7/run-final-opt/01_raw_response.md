TARGETS: encodeBase64String(byte[]), encodeBase64(byte[]), encodeToString(byte[]) output termination  
TARGETS: RFC 4648 encoding for unchunked input; padding behavior for 1-byte and ordinary inputs  
ORACLES: Trigger expectations: "Hello World" -> SGVsbG8gV29ybGQ=; "f" -> Zg==  
CASES: Unchunked 1-byte input must end at padding, with no trailing line separator  
CASES: Unchunked "Hello World" must exactly match expected Base64 without appended newline  
CASES: Boundary inputs around 3-byte groups verify padding/no-padding output termination  
RISKS: Chunked APIs may legitimately use line separators; distinguish chunked from unchunked behavior  
RISKS: Context lacks source implementation and broader existing-test expectations for empty/null inputs