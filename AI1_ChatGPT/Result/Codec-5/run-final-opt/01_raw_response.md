TARGETS: Base64 decode paths used by Base64InputStream/Base64OutputStream with CODEC-98 input  
ORACLES: Trigger tests must complete without NullPointerException  
CASES: Decode valid Base64 through input and output stream wrappers  
CASES: Decode empty, short, and padding-boundary Base64 data through stream wrappers  
CASES: Exercise stream finalization/close after decode input  
RISKS: Exact CODEC-98 payload and intended decoded bytes are not provided  
RISKS: Only Base64 is modified; stream-wrapper implementation context is unavailable