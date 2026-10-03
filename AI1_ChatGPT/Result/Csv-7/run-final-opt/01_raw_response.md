TARGETS: CSVParser constructor/initializeHeader() when header names are read or supplied by CSVFormat.  
ORACLES: Trigger test requires IllegalStateException for duplicate header entries (CSV-112).  
CASES: Parse input whose header record repeats a non-empty name; assert construction/parsing throws IllegalStateException.  
CASES: Use CSVFormat header configuration containing duplicate names, if accepted by the available format API.  
CASES: Cover distinct headers as normal control; header map should remain usable without exception.  
RISKS: Header source and duplicate-handling options are not fully shown; avoid assuming map contents or message text.