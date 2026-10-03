TARGETS: FastDateFormat serialization round-trip (writeObject/readObject) for patterns containing
padded-number fields (HH, mm, ss, SSS, dd, MM, etc.)
ORACLES: No SerializationException thrown; deserialized object equals original; format(long) and
format(Date) produce identical strings
CASES: getInstance("HH:mm:ss.SSS") default tz; same with Locale.US;
getInstance("yyyy-MM-dd'T'HH:mm:ss"); border: pattern without padded fields (e.g., "yyyy");
null/empty pattern
RISKS: Only PaddedNumberField triggers NotSerializableException; TwoDigitYearField and TextField
might not fail; static caches are not serialized and may cause inconsistency if not cleared