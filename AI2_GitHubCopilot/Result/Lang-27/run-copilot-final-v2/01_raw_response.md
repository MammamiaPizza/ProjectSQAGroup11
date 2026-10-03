TARGETS: createNumber(String) hex/octal prefix handling, substring indices after trimming 0x/0X,
sign and suffix.
ORACLES: Javadoc: valid number string returns Number; throw NumberFormatException for invalid, never
IndexOutOfBounds.
CASES: Normal: "123","0x1F","-0xAB","1.5f","0","0177". Boundary:
"0x","0X","-0x","+0x","0x-","0x+","0xG","0x ".
CASES: Suffix: "1l","1L","1f","1F","1d","1D". Hex mixed-case "0XFF". Octal "00". Error: null, "", "
 ".
CASES: Decimal overflow boundaries like "999999999999999999999999999999". Edge: "0x7FFFFFFFFFFFFFFF"
(valid).
RISKS: Only trigger is testCreateNumber; fix must validate length before substring for all prefix
variants to avoid OOB.
RISKS: isAllZeros could also be vulnerable with empty string passed from createNumber; ensure no OOB
there.