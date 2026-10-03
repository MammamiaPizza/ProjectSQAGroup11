TARGETS: org.apache.commons.codec.binary.Base64 constructors, encode(byte[]), encodeBase64(byte[]),
isUrlSafe()
ORACLES: Roundtrip encode→decode matches input; RFC 4648 Base64 alphabet; URL-safe alphabet vs
standard alphabet
CASES: Empty byte array, single byte, multi-byte (1-3 sizes), binary data with zeros/FF, lineLength
boundaries, URL-safe flag on/off
RISKS: Only API/signatures visible; no internal encodeBase64 static impl details;
chunked/lineSeparator behavior unclear from context