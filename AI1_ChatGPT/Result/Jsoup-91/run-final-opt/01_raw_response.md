TARGETS: UncheckedIOException(IOException) and ioException() cause exposure.  
TARGETS: CharacterReader buffering/read paths when its Reader throws IOException.  
ORACLES: Trigger tests require binary parsing/connection failures to throw rather than silently succeed.  
ORACLES: UncheckedIOException.ioException() should return the supplied IOException cause.  
CASES: Reader that throws IOException on initial read and during buffer refill.  
CASES: Normal String/Reader consumption at EOF and around buffer boundaries.  
RISKS: CharacterReader bufferUp is private; exercise it through public reading/consumption methods.  
RISKS: No source diff or explicit exception-message/type assertions beyond supplied context.