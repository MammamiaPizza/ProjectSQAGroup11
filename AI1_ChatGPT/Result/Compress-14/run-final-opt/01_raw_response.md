TARGETS: TarUtils.parseOctal(byte[],int,int); header-time parsing used by TarArchiveInputStream.  
ORACLES: Existing workaroundForBrokenTimeHeader expects parsing to avoid "Error detected parsing the header".  
CASES: Octal fields with leading/trailing NUL or space bytes around otherwise valid digits.  
CASES: Valid zero and positive octal values at offset/length boundaries.  
CASES: Invalid non-octal bytes should retain defined parse-error behavior.  
RISKS: Context lacks TarUtils implementation and exact malformed time-header byte layout.