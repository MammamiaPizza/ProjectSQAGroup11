TARGETS: CSVPrinter constructor header emission; print/println record state; printRecord(s) output sequencing.  
ORACLES: Trigger testHeader expects configured header values as first CSV record with record separator.  
CASES: Construct with header C1,C2,C3; verify exact header text before subsequent printed records.  
CASES: Header plus printRecord values; verify one header only and proper record separation.  
CASES: Empty/no header format; verify construction does not add an unintended record.  
RISKS: Available context omits CSVFormat header API/details and full expected trigger output.