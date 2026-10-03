TARGETS: doubleMetaphone(String, boolean) with alternate=true; DoubleMetaphoneResult.getAlternate();
conditionCH1.
ORACLES: Known expected alternate: "ANJR" for "Angier" from CODEC-84 bug; no other reference values.
CASES: "Angier","Angus","Ginger","Gigi","George","Gino"; boundary: maxCodeLen=4, empty/null input;
long input.
RISKS: Only one known correct alternate; G->J/K ancillary rules unknown; may miss regression for
other 'G' patterns.