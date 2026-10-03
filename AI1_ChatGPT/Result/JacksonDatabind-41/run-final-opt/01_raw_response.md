TARGETS: TypeFactory type resolution, especially _fromAny and deprecated Type handling reached by constructType  
ORACLES: DeprecatedTypeHandling1102Test::testDeprecatedTypeResolution; absence of IllegalArgumentException for [null]  
CASES: Resolve trigger’s deprecated type path; verify resulting JavaType properties exposed by the trigger test  
CASES: Boundary: null encountered during nested Type resolution; preserve normal Class/parameterized-type resolution  
RISKS: API excerpt is truncated; exact deprecated Type implementation and intended null fallback are not shown