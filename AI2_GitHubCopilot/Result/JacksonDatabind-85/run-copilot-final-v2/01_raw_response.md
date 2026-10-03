TARGETS: DateTimeSerializerBase._asTimestamp(SerializerProvider)
TARGETS: DateTimeSerializerBase.serialize(T,JsonGenerator,SerializerProvider) w/ custom format
TARGETS: DateTimeSerializerBase.withFormat(Boolean,DateFormat) to set _useTimestamp and
_customFormat
ORACLES: Expected serialized string from format.format(date) when DateFormat lacks pattern
ORACLES: _asTimestamp() must return false if _customFormat != null, regardless of hasPattern()
ORACLES: Expected JSON is not timestamp array; text must match format's output for the given Date
CASES: Date with custom DateFormat without pattern (e.g., DateFormat.getDateTimeInstance())
CASES: Date with custom DateFormat, explicit pattern; ensure textual output not timestamp
CASES: Date with _useTimestamp forced true overrides custom format -> numeric timestamp array
RISKS: Local/TimeZone differences break string comparison; use fixed Locale,TimeZone, and known Date