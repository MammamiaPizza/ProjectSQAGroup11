TARGETS: encodeMimeName(String) in HttpConnection (private, encodes header values with non‑ASCII)
TARGETS: header(String name, String value) → triggers encodeMimeName on the value
ORACLES: No ArrayIndexOutOfBoundsException; output follows RFC 2047 “=?charset?B?base64?=” when
needed
ORACLES: Expected encoding computed by manual UTF‑8 → Base64 wrapping; ASCII values remain untouched
CASES: Normal: “Müller” → encoded‑word; simple ASCII → unchanged; empty value → no exception
CASES: Boundary: single‑char, value containing only ‘?’, ‘=’, or spaces, value with
commas/semicolons
CASES: Edge: value already encoded (e.g., “=?UTF‑8?B?…?=”) should not be double‑encoded
CASES: Multiple consecutive non‑ASCII segments to verify splitting/concatenation logic
RISKS: No bug‑fixed version available; expected‑encoding oracle must reference RFC 2047 alone