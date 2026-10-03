TARGETS: JsonGenerator embedded-object handling, especially byte[] passed through object-writing paths.  
ORACLES: Trigger tests expect no "No native support for writing embedded objects" JsonGenerationException.  
CASES: Write a byte[] as an embedded/object value using a JSON generator; verify successful binary JSON output.  
CASES: Exercise embedded non-binary object behavior to retain unsupported-operation/error semantics where applicable.  
CASES: Include empty and non-empty byte[] values to cover binary delegation boundaries.  
RISKS: Available context omits concrete generator implementations and full writeObject/writeEmbeddedObject signatures.