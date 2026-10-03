TARGETS: CategoryPlot/XYPlot removeDomainMarker and removeRangeMarker behaviors.  
ORACLES: Trigger tests define expected successful marker removal; failures must not throw NPE.  
CASES: Add a domain marker, remove it, verify removal/result and notification behavior if exposed.  
CASES: Add a range marker, remove it, verify removal/result and notification behavior if exposed.  
CASES: Exercise default marker layer/index paths implicated by all four trigger tests.  
RISKS: API excerpt is truncated; marker overloads, return values, layers, and event assertions are unspecified.