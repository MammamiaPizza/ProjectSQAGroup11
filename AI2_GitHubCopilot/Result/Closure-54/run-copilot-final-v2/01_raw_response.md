TARGETS: TypedScopeCreator property resolution for 'this' inside constructor of a class extending an
unknown superclass.
TARGETS: FunctionType.getPrototype()/setPrototypeBasedOn handling when superclass type is
unresolvable.
ORACLES: testIssue537a expects arg-mismatch error ("called with 1 argument(s)... max 0") not
"Property baz never defined on Bar".
ORACLES: testIssue537b expects same arg-count error for Bar.prototype.baz.
ORACLES: testPropertyOnUnknownSuperClass2 expects unknown type "?" not "number" for property on
instance extended from unknown superclass.
CASES: Normal: class extends known class; call this.method() with wrong args → arg-mismatch error
(existing behavior).
CASES: Boundary: class extends undefined identifier; call this.prop() → check call signature, not
"Property never defined".
CASES: Boundary: inside constructor of class extending unknown, type of this and this.prop should be
"?".
RISKS: Changing TypedScopeCreator may unintentionally alter prototype chains for known superclasses;
regression tests needed.
RISKS: Limited visibility: FunctionType changes could affect interface resolution; test generation
must stay conservative.