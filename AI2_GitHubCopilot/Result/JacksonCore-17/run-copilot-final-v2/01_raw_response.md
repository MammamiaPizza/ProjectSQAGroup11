TARGETS: UTF8JsonGenerator.writeRaw(String,int,int); writeRaw(String); writeRaw(char[],int,int)
ORACLES: no split surrogate at raw-input end; preserve high/low surrogate pairing across writeRaw
boundaries
CASES: lone high surrogate as last char; valid pair spanning buffer/segment boundary; lone low
surrogate; empty raw
RISKS: bug in surrogate handling at input end; private helpers _outputSurrogates/_writeUTF8Segments
not directly testable