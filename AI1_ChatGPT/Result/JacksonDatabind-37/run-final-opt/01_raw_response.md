TARGETS: SimpleType construction, narrowing, handler/static-typing variants, canonical/signature/equality behavior.  
TARGETS: Integration path exercised by Objecid1083Test::testSimple object-id deserialization.  
ORACLES: Existing trigger expects no UnrecognizedPropertyException for JsonMapSchema field "name".  
ORACLES: Public method contracts implied by returned types, documented exceptions, and existing test assertions.  
CASES: Deserialize the trigger’s simple object-id JSON/schema scenario and verify "name" is accepted.  
CASES: construct/constructUnsafe for ordinary classes; verify SimpleType is non-container.  
CASES: Deprecated construct rejects Map, Collection, and array classes with IllegalArgumentException.  
CASES: Handler/static-typing/content-type variants preserve expected type metadata and equality distinctions.  
RISKS: Protected constructors/_narrow require indirect coverage through public APIs or same-package tests.  
RISKS: No source diff or full JsonMapSchema/JSON fixture is provided; derive inputs from the trigger test only.