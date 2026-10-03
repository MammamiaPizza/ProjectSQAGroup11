TARGETS: Attribute constructors and getValue() handling of boolean-attribute values  
ORACLES: Trigger test expects empty string, not null, for boolean attribute value  
CASES: Construct boolean attribute with null value; verify getValue() returns ""  
CASES: Verify returned/set value behavior for explicit empty-string boolean value  
RISKS: Constructor normalization may affect setValue(), html(), equality, and serialization  
RISKS: No source/test body supplied beyond the named trigger and failure assertion