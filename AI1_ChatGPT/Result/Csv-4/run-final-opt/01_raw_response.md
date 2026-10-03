TARGETS: CSVParser.getHeaderMap(), constructor/header initialization, and record parsing without configured headers  
ORACLES: Trigger testNoHeaderMap; getHeaderMap behavior when CSVFormat provides no header map  
CASES: Parse simple records with no header configuration; call getHeaderMap before/after iteration  
RISKS: NPE from absent header map; context omits CSVFormat behavior and full CSVParser implementation