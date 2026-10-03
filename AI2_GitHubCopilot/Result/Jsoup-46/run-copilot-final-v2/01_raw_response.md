TARGETS: Entities.escape with Shift_JIS OutputSettings; private canEncode; CoreCharset.byName
ORACLES: escape output must not contain '?' for encodable chars; roundtrip escape→unescape preserves
input; use entity map for unencodable chars
CASES: chars at Shift_JIS boundary (e.g., \u00A5, \uFF5E); ASCII-only; empty string; mixed
named-entity+raw text; non-BMP supplementary chars
RISKS: canEncode is private, test via escape; Shift_JIS behavior varies by JDK; bug may depend on
specific charset encoder fallback handling