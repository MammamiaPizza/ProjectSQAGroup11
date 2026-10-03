TARGETS: DateTimeSerializerBase.serialize() timestamp/string logic; withFormat(); _asTimestamp().
ORACLES: Expected output when WRITE_DATES_AS_TIMESTAMPS=false (default) is ISO-8601 string; true
gives numeric timestamp.
CASES: Default shape (no config) -> string; explicit true -> number; false -> string; custom pattern
DateFormat.
CASES: Boundary: null input -> null/empty output?; far dates; epoch zero.
RISKS: Abstract class; test via DateSerializer or ObjectMapper; internal _useTimestamp not directly
inspectable.TARGETS: DateTimeSerializerBase.serialize() timestamp/string logic; withFormat();
_asTimestamp().
ORACLES: Expected output when WRITE_DATES_AS_TIMESTAMPS=false (default) is ISO-8601 string; true
gives numeric timestamp.
CASES: Default shape (no config) -> string; explicit true -> number; false -> string; custom pattern
DateFormat.
CASES: Boundary: null input -> null/empty output?; far dates; epoch zero.
RISKS: Abstract class; test via DateSerializer or ObjectMapper; internal _useTimestamp not directly
inspectable.