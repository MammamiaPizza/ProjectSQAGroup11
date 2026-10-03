TARGETS: CheckSideEffects.process, hotSwapScript, visit, protectSideEffects, addExtern, StripProtection  
ORACLES: CheckSideEffectsTest.testUselessCode expects exactly one warning for its triggering input  
CASES: Useless-code input from testUselessCode should produce one warning, not zero  
CASES: Exercise normal process traversal and hot-swap traversal paths if test fixtures expose both  
RISKS: Modified source behavior/details are unavailable; derive inputs and warning assertions only from existing tests  
RISKS: No warning type, message, or public constructor/API is specified in the provided context