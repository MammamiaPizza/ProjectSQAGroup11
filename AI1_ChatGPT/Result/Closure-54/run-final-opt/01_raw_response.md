TARGETS: TypedScopeCreator property/type declaration for methods on unknown superclass prototypes.
TARGETS: FunctionType call arity diagnostics and prototype/instance type behavior.
ORACLES: Triggered tests' exact expected diagnostics: Foo/Bar method called with 1, requires 0.
ORACLES: Unknown-superclass property type is expected "?" rather than inferred "number".
CASES: Foo.prototype.method and Bar.prototype.baz declarations with calls supplying one argument.
CASES: Property assignment/access through an unknown superclass; verify unknown type propagation.
RISKS: APIs/implementation bodies are truncated; test via existing compiler test harness only.
RISKS: Do not derive expectations from another version; supplied trigger assertions are the oracle.