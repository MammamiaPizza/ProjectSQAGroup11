TARGETS: TypeValidator error messages for unimplemented interface property and overridden property.
ORACLES: Expected type name in testIssue1047 is "C2" (user-visible), not internal "C3.c2_".
CASES: Missing property on class implementing interface; forward-declared type name substitution.
RISKS: Only testIssue1047 triggers bug; containsForwardDeclaredUnresolvedName likely involved;
limited context.