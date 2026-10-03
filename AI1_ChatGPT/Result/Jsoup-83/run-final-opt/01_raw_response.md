TARGETS: CharacterReader consumeTo/consumeToAny and buffering positions at '<' boundaries.  
TARGETS: TokeniserState handling of '<' inside tag/attribute parsing as a new tag start.  
ORACLES: Existing trigger expected DOM/HTML strings define required tokenisation outcomes.  
CASES: Parse rough attributes containing '<'; verify following p/a elements are separately formed.  
CASES: Tokenise/parse '<' in a tag before p/div/span; verify p closure and nested div/span output.  
CASES: Boundary '<' at buffer boundary and immediately followed by tag-like input, if controllable.  
RISKS: CharacterReader buffer state may skip or retain '<', affecting TokeniserState transitions.  
RISKS: Context lacks exact input strings and direct TokeniserState public entry points.