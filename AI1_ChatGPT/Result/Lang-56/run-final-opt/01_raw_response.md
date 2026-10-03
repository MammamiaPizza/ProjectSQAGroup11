TARGETS: FastDateFormat serialization, especially instances whose patterns select PaddedNumberField.  
ORACLES: Java serialization round-trip must not throw NotSerializableException; restored formatter formats equivalently.  
CASES: getInstance("yyyy-MM-dd") serialize/deserialize, then format a fixed Date and compare pre/post output.  
CASES: Exercise padded numeric widths/pattern fields implicated by PaddedNumberField (for example yyyy, MM, dd).  
CASES: Round-trip cached getInstance(pattern, timezone, locale) with fixed timezone/locale and fixed date.  
RISKS: Existing trigger only identifies serialization failure; exact intended serializable nested-rule coverage is truncated.