TARGETS: processRequireCall, processProvideCall, verifyProvide, processDefineCall,
processBaseClassCall
ORACLES: VarCheck reports no undeclared vars when goog.provide is called for required namespace
ORACLES: No error when goog.require appears after goog.provide in compilation order
CASES: Normal: provide then require for same namespace
CASES: Boundary: require before provide (should still be okay after fix?)
CASES: Multi-file: require in one file, provide in another, processed together
CASES: Base class with goog.inherits and goog.require
RISKS: Internal state like exportedVariables may not be populated correctly; tests only detect
through VarCheck