TARGETS: CheckSideEffects.process, hotSwapScript, visit, protectSideEffects, addExtern, StripProtection  
ORACLES: Existing CheckSideEffectsTest::testUselessCode expects one warning for its input  
CASES: Useless-code AST/input path exercised by testUselessCode should report exactly one warning  
CASES: Verify warning collection is not empty when the trigger input is processed  
RISKS: Context lacks the trigger source, diagnostic type/message, and intended behavior for other expressions  
RISKS: Private helpers require behavior to be observed through process or hotSwapScript