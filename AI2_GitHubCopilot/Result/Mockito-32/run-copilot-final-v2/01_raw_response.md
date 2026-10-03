TARGETS: SpyAnnotationEngine.process()
TARGETS: createMockFor() name-setting logic
ORACLES: spy.toString() equals field name per Mockito default naming
ORACLES: spy.toString() does NOT start with generic "Mock for ..."
CASES: Normal: @Spy Object f; → spy.toString() must be "f"
CASES: Boundary: field name = "my$Field" (special chars)
CASES: Boundary: inner class field
CASES: Error: field already set (spy on real object)
RISKS: Expected name may include mock-creation suffix if name clash (risk ignored in test)