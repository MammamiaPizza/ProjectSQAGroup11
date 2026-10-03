TARGETS: checkNameVisibility on GETPROP/SETPROP nodes for private property override detection.
ORACLES: Error "Overriding private property of {0}.prototype."; JSError count from
CheckAccessControls.visit (expected 1).
CASES: Non-override private property access; override of @private via prototype assignment from
outside class; nested function assigning to proto private property.
CASES: Override via this.prop= inside constructor (no error); subclass assigning to private
prototype property (error or not depending on scope).
CASES: Object literal override of private property; @private method override on prototype.
RISKS: No source for exact override detection so expected behavior must be inferred from bug
context; false positives for valid protected access possible.
RISKS: The trigger tests imply a missing error for proto private property overrides; must avoid
creating false positives for legitimate @private assignments within class.