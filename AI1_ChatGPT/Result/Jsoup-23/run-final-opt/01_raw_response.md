TARGETS: CharacterReader and Tokeniser handling of character references with letters followed by digits.  
ORACLES: EntitiesTest::letterDigitEntities assertion is the only stated expected-result source.  
CASES: References &sup1;, &sup2;, &sup3; must not be parsed as the shorter &sup; entity.  
CASES: References &frac14;, &frac12;, &frac34; exercise digit-suffixed entity-name boundaries.  
RISKS: Current behavior consumes a valid name prefix, producing ⊃1; / escaped ampersands instead of expected text.  
RISKS: Context omits accessible methods and the precise input/assertion source for the trigger.