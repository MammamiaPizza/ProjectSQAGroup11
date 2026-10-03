TARGETS: TypedScopeCreator scope creation and enum discovery/declaration during traversal.  
ORACLES: Existing trigger expects a TypeCheck warning for Issue586 input.  
CASES: Enum initializer using an object literal; verify no enum-initializer warning.  
CASES: Enum initializer not an object literal; verify warning text: "enum initializer must be an object literal or an enum".  
RISKS: Private traversal helpers require compiler/source-based integration testing.  
RISKS: Context lacks Issue586 source and exact warning type/location.