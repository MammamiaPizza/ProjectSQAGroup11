TARGETS: ComplexFormat.parse(String), parse(String,ParsePosition), parseNextCharacter, parseNumber
ORACLES: Parse must throw ParseException (no StringIndexOutOfBounds) when imaginary character is
absent at end of input; error offset should point to missing char
CASES: "1+2i" (normal), "1+" (missing imag), "1 - i" (sign+imag only), "" (empty), "123" (real
only), "1 2" (two reals)
CASES: " + i" (leading whitespace+sign+imag), "-1+" (trailing op), custom char "1+2j" with
setImaginaryCharacter('j')
RISKS: Exact ParseException message/offset not specified; locale-specific number parsing may shift
positions; setImaginaryCharacter must be tested