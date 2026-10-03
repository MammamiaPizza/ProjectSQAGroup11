TARGETS: PosixParser.flatten with stopAtNonOption true around non-options and "--" terminator  
TARGETS: burstToken/processOptionToken handling grouped options and option arguments  
ORACLES: Existing trigger assertions: parsed argument is "println 'hello'", not "--"  
ORACLES: Existing trigger assertion: with -b, remaining argument is "foo", not "--"  
CASES: Parse "-b foo" with stopAtNonOption=true; retain -b and expose foo as argument  
CASES: Parse non-option command text after options; it must not be replaced by "--"  
CASES: Boundary: explicit "--" should stop option parsing and preserve following arguments  
RISKS: Only failure summaries and PosixParser signatures are available; option definitions are unspecified