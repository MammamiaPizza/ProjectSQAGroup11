TARGETS: TreeTraversingParser.getBinaryValue(Base64Variant) — decodes TextNode content as binary
ORACLES: Known base64 strings (e.g., "AA==" → [0]) and their expected decoded byte arrays (per
Base64Variant spec)
ORACLES: Base64Variant.decode(String) provides reference output to verify parser behavior
CASES: URL-safe variant (MODIFIED-FOR-URL) with single-byte encoded content (data length 1)
CASES: Standard variant multi-byte values, boundary: empty TextNode, missing/extra padding
CASES: Invalid base64 characters should trigger JsonParseException
RISKS: Only TextNode binary access tested; no ArrayNode/other node binary tests
RISKS: Expected behavior for MODIFIED-FOR-URL variant and padding edge cases unclear without fixed
source
RISKS: Limited context—cannot inspect getBinaryValue changes; rely on external Base64Variant
correctness