TARGETS: Partial constructors and field ordering/validation for era and year types  
TARGETS: UnsupportedDurationField.compareTo and duration-field ordering behavior  
ORACLES: Trigger test expects no duplicate-types IllegalArgumentException for era/year  
CASES: Construct Partial with era/year fields and valid values; assert construction succeeds  
CASES: Exercise ordering where both base and argument duration fields have no range  
RISKS: Trigger details omit exact constructor inputs, chronology, and post-construction assertions