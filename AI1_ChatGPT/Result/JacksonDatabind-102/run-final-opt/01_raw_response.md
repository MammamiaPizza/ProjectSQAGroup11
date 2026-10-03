TARGETS: DateTimeSerializerBase format override handling used by SqlDateSerializationTest.  
ORACLES: Trigger expects SQL date JSON string `"1980+04+14"`, not timestamp `324547200000`.  
CASES: Config override with STRING shape/custom date format for java.sql.Date serialization.  
CASES: Verify override selects string serialization despite default timestamp-oriented serializer behavior.  
RISKS: Locale/time-zone presence and DateFormat reuse may affect formatted output.  
RISKS: Context lacks concrete override API/setup and broader expected behaviors.