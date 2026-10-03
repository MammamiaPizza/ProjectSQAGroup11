TARGETS: Base64 streaming encode/decode state used by Base64InputStream and Base64OutputStream.
ORACLES: Trigger assertions: empty Base64InputStream read must return EOF (-1), not CR (13).
ORACLES: Trigger assertion: empty Base64OutputStream encoding must produce expected empty output.
CASES: Empty input stream: first read(), buffered read, and repeated reads return EOF.
CASES: Empty output stream: close/flush without writes emits no Base64 data or line separator.
CASES: Non-empty streaming input/output preserves Base64 encoding behavior around finalization.
RISKS: Line-length/chunking state may emit separator bytes for empty or boundary-length streams.
RISKS: Context provides Base64 APIs but not Base64InputStream/Base64OutputStream constructor details.