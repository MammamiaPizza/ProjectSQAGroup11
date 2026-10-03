TARGETS: TokenBuffer POJO/object writing and parser token/value exposure used by POJO conversion.  
ORACLES: Trigger expectation: converted POJO is exposed as Object, not POJO.  
CASES: Buffer a POJO via ObjectCodec, parse/deserialize it, and assert resulting node/value type is Object.  
CASES: Exercise nested POJO properties and scalar fields through TokenBuffer serialization/parsing.  
CASES: Check firstToken/asParser token sequence for buffered object start, field names, values, end.  
RISKS: Available context omits complete writeObject/writePOJO signatures and exact conversion setup.