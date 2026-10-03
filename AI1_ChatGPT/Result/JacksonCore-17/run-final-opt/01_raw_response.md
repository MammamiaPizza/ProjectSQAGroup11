TARGETS: UTF8JsonGenerator.writeRaw(String) and writeRaw(String,int,int) surrogate handling across segments  
TARGETS: _outputRawMultiByteChar(int,char[],int,int) and _outputSurrogates(int,int)  
ORACLES: Trigger test must not throw JsonGenerationException for raw string containing surrogate pair  
ORACLES: Output bytes/text should preserve the raw supplementary character encoding  
CASES: writeRaw(String) with a valid surrogate pair at a segment boundary  
CASES: writeRaw(String,offset,len) whose final selected char is a high surrogate followed outside range  
CASES: valid pair wholly within selected range; ordinary ASCII around the pair  
RISKS: Exact output and intended behavior for unpaired/split surrogates are not specified here  
RISKS: Source context is truncated; internal segment-size boundary details are unavailable