TARGETS: CreatorCollector.addPropertyCreator, verifyNonDup, constructValueInstantiator  
ORACLES: Creator1476Test::testConstructorChoice must deserialize SimplePojo without missing 'intField' error  
CASES: Competing creator constructors where the intended property-based creator includes intField  
CASES: Property-based creator registration preserves its CreatorProperty[] arguments for lookup  
CASES: Constructor-choice precedence when explicit/non-explicit creators overlap  
RISKS: Duplicate-creator resolution may replace the selected property creator or discard property metadata  
RISKS: Context lacks SimplePojo source, JSON input, and exact expected field values