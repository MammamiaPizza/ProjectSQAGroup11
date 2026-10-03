TARGETS: BZip2CompressorInputStream.read(byte[],int,int) partial reads on truncated bzip2 input  
TARGETS: read() and EOF/error propagation during block decoding  
ORACLES: PythonTruncatedBzip2Test::testPartialReadTruncatedData and COMPRESS-253  
CASES: Read truncated data with a destination buffer smaller than remaining decoded output  
CASES: Repeat partial bulk reads until EOF or IOException; compare observed termination behavior  
CASES: Exercise read(byte[],offs,len) with nonzero offset and short length on truncated input  
RISKS: Truncated fixture bytes and exact expected decoded content are not provided  
RISKS: No non-buggy version may be used to infer behavior