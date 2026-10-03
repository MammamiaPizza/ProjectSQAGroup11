TARGETS: ReaderBasedJsonParser._parseNumber (and related), UTF8StreamJsonParser._parseNumber (and
related), number validation in both.
ORACLES: JSON spec: no leading zeros (except for "0"), no leading "+", exponent must have digits, no
multiple decimal points.
CASES: Normal: "0","-0","123","1.5e2". Mangled: "01","00","+1","1.","1e","1.2.3","1e+","1e-","-.5".
Boundary: deep recursion, large exponents.
RISKS: Exact mangled-number character sequences expected by test are unknown; must ensure all
candidate mangled inputs throw JsonParseException, not just leading zeros.