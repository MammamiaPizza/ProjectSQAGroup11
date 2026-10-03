TARGETS: NumberInput.parseBigDecimal(String/char[], offset, len); TextBuffer.contentsAsDecimal()  
ORACLES: Trigger expects exception message containing "can not be represented as BigDecimal" for NaN parsing  
CASES: NumberInput.parseBigDecimal("NaN") and char[] slice containing "NaN"  
CASES: TextBuffer resetWithString/resetWithShared/resetWithCopy containing "NaN", then contentsAsDecimal()  
CASES: Valid decimal text through NumberInput and TextBuffer; verify returned BigDecimal value  
CASES: Boundary slices with nonzero offset/length around invalid numeric text  
RISKS: Trigger is parser-level; available context does not expose parser configuration or exact exception type  
RISKS: Do not assert unsupported behavior for parseInt/parseLong or TextBuffer allocation/segment internals