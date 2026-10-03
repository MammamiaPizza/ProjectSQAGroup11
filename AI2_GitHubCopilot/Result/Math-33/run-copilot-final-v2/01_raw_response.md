TARGETS: initializeColumnLabels, normalize, normalizeConstraints, createTableau,
dropPhase1Objective, getBasicRow, getSolution
ORACLES: Tableau data equals expected matrix; columnLabel size matches width; getSolution
PointValuePair; normalized constraint RHS signs/flips
CASES: maximize vs minimize flag; equality/GE/LE constraints; phase-1 drop after artificial vars;
zero decision variables; duplicate/mixed relationships
RISKS: Bug-trigger testMath781 not visible; only signatures given; exact contract of
normalize/dropPhase1Objective must be inferred from method names and Javadoc conventions