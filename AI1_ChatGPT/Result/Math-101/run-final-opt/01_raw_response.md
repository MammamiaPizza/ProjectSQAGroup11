TARGETS: ComplexFormat.parse(String) and parse(String, ParsePosition) imaginary-suffix handling  
ORACLES: Existing ComplexFormatTest and FrenchComplexFormatTest forgotten-imaginary-character cases  
CASES: Parse valid real-plus-imaginary input with configured/default imaginary character  
CASES: Parse input ending after imaginary coefficient but missing imaginary character  
ORACLES: Missing suffix must report parse failure, not StringIndexOutOfBoundsException  
CASES: Repeat missing-suffix input using French locale format  
RISKS: ParsePosition end-of-input access in parseNextCharacter/whitespace parsing  
RISKS: Context lacks exact expected ParsePosition index/errorIndex assertions