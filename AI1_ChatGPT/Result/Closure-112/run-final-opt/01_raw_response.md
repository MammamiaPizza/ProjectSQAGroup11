TARGETS: TypeInference call/new inference, template substitution, and parameter type updates.  
ORACLES: Existing TypeCheckTest::testIssue1058 and ::testTemplatized11 warning expectations.  
CASES: Generic/templatized calls and constructors whose inferred argument/result types must avoid warnings.  
CASES: Calls exercising backwardsInferenceFromCallSite and updateTypeOfParameters.  
RISKS: TypeInference methods are private; tests should use compiler/type-check behavior, not direct calls.  
RISKS: Context lacks source diff and exact JavaScript inputs/expected warnings; derive only from named triggers.