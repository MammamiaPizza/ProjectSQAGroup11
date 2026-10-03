TARGETS: process(), visit(), processProvideCall(), processRequireCall() namespace provision/require tracking  
ORACLES: VarCheckTest trigger expects one error: required "namespace.Class1" namespace never provided  
CASES: goog.require of unprovided namespace reports the missing-namespace error after Closure pass  
CASES: goog.provide matching a required namespace prevents an undeclared-variable/missing-provider error  
CASES: nested namespace names (for example namespace.Class1) preserve full qualification in diagnostics  
RISKS: Context exposes no constructor/configuration or exact compiler-input harness APIs  
RISKS: Do not infer behavior for defines, inherits, CSS mappings, or hot-swap beyond listed methods