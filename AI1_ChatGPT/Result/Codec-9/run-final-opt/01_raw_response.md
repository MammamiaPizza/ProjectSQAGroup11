TARGETS: Base64.encodeBase64(byte[], boolean, boolean, int) max-result-size validation.
ORACLES: Base64Test::testCodec112 and standard unchunked Base64 output length.
CASES: Unchunked 3-byte input with maxResultSize 4 must permit its 4-byte encoded result.
CASES: Boundary maxResultSize equal to encoded size; one below should be rejected.
CASES: Compare chunked versus unchunked sizing near chunk-separator boundaries.
RISKS: Trigger reports estimated size 6 for allowed maximum 4, suggesting unwanted separator sizing.
RISKS: Context lacks exact test inputs and intended exception details beyond the trigger.