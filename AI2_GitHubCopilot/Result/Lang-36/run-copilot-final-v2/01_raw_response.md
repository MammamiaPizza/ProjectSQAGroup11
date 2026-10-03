TARGETS: isNumber(String) and createNumber(String) behavior for strings with trailing/leading
decimal points.
ORACLES: Validate against Double.valueOf/parseDouble: "2." should yield Double 2.0; isNumber should
return true for valid Double parsable strings.
CASES: "2.", ".2", "2.0", "0.", ".0", "-3.", "123.", "2.5", "2.0D", "2.f", "2..3", "2", ".", "",
null.
RISKS: No fixed version; underlying createFloat/createDouble/createBigDecimal/isAllZeros may need
coverage; locale-independent decimal parsing assumed; "." alone might still be invalid.