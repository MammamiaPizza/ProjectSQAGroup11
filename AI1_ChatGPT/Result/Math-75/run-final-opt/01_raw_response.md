TARGETS: Frequency.getPct(Object), getPct(Comparable), and overload delegation/count lookup  
ORACLES: Trigger: after known frequencies, Object value "three" has getPct(Object)==0.5, not 1.0  
CASES: Add comparable values with repeated "three"; assert count/sum and Object/Comparable getPct agreement  
CASES: Verify absent value returns zero percentage; verify empty frequency percentage behavior if established by tests  
RISKS: Overload resolution may route Object arguments differently from Comparable and primitive overloads  
RISKS: Context supplies only the failing pct assertion; no source-level implementation or broader expected results