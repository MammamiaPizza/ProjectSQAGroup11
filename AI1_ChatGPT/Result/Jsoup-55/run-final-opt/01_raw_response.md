TARGETS: TokeniserState handling of '/' while parsing attribute names and self-closing start tags.  
ORACLES: Trigger expectation: self-closing parsing must not ignore the final attribute-name character.  
CASES: Parse attributes containing '/' followed by normal name characters; verify complete attribute names are retained.  
CASES: Boundary: '/' at attribute-name end before tag close or self-closing delimiter.  
RISKS: Context exposes only TokeniserState internals; public parsing entry points and exact outputs are not provided.