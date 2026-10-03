TARGETS: CharacterReader consumeUntil/matches for entity name letters+digits;
Tokeniser.consumeCharacterReference lookup and emit via charBuffer.
ORACLES: letterDigitEntities test expects entities like &sup1; &frac14; are correctly parsed, not
returned as escaped "&" or garbled characters.
CASES: Normal entities (&), letter-digit (&sup1; &frac14;), unknown (&a1;), numeric (&#65;),
incomplete (&sup1), position (start/mid/end).
RISKS: Exact internal parsing method signatures not provided; only bug-trigger test and modified
classes known; limited API visibility.