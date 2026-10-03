TARGETS: DefaultDateTypeAdapter.read(JsonReader), especially token handling before date parsing  
ORACLES: Triggers require unexpected token and JSON null not to throw "date should be a string value"  
CASES: Read STRING valid date; verify Date/Timestamp/java.sql.Date conversion paths  
CASES: Read NULL and assert null result; read non-string token and assert JsonParseException  
CASES: Write null emits JSON null; write date emits a JSON string  
RISKS: Exact accepted date formats/parsed values depend on configured DateFormat and locale  
RISKS: Adapter is package-private; tests need com.google.gson package access  
