TARGETS: CSVRecord.get(String), especially mapped names whose index exceeds this record's values  
ORACLES: CSV-96 trigger expects IllegalArgumentException, not ArrayIndexOutOfBoundsException  
CASES: inconsistent record: header mapping contains a name mapped beyond record size; get(name)  
CASES: consistent mapped name returns its indexed value; isConsistent reports mapping/value-size agreement  
CASES: missing header mapping, unmapped name, and null name if supported by existing behavior  
RISKS: only signatures and trigger summary are provided; exact messages and constructor/setup APIs are unavailable