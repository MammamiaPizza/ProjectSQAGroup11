TARGETS: _asTimestamp(SerializerProvider) logic for timestamp vs string decision
TARGETS: withFormat(Boolean, DateFormat) propagation of config override shape
TARGETS: serialize() branch when _asTimestamp returns false calls _serializeAsString
ORACLES: testSqlDateConfigOverride expects "1980+04+14" for custom format on sql.Date
ORACLES: Expected: shape=STRING or _useTimestamp=false causes string serialization
CASES: Override with shape=STRING, custom format (yyyy+MM+dd) -> string output
CASES: Override with shape=NUMBER or no config -> numeric timestamp output
CASES: Override with _useTimestamp=null inherited from shape=STRING -> string output
CASES: Default DateTimeSerializerBase behavior without override -> timestamp output
RISKS: Cannot see buggy _asTimestamp implementation; failure implies shape ignored