TARGETS: TypeInference.traverseNew and backwards inference from constructor call sites.  
ORACLES: Existing TypeInferenceTest.testBackwardsInferenceNew comparison expectation.  
CASES: new-call argument infers object property foo as (number|undefined).  
CASES: Assert inferred scope/type string is "{foo: (number|undefined)}", not "{}".  
RISKS: TypeInference methods are private; test through compiler/type-inference test harness.  
RISKS: Context lacks patch and surrounding test fixtures; derive expectations only from trigger.