TARGETS: DevirtualizePrototypeMethods.rewriteDefinition (maps this to $self)
TARGETS: DevirtualizePrototypeMethods.fixFunctionType (updates rewritten method type)
ORACLES: $self variable JSType must equal original 'this' type (a) after rewrite
ORACLES: compiled output matches expected $self type string (e.g., $self = a)
ORACLES: no unexpected type-check warnings after devirtualization (testGoodExtends9)
CASES: prototype method on named type, called on instance (normal case)
CASES: subclass extends parent with devirtualized method (boundary: inheritance)
CASES: this type is unknown/nullable → $self may be null (error vs boundary)
RISKS: fix only verified for simple FunctionType; may miss union/unknown this types
RISKS: type propagation may fail if $self is reassigned inside rewritten method body