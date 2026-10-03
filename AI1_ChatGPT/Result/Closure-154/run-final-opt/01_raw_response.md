TARGETS: TypeCheck interface inheritance validation; TypeValidator interface-property implementation mismatch reporting.  
ORACLES: Existing trigger expects a warning; diagnostics/messages in TypeCheck/TypeValidator are the only stated source.  
CASES: Interface extending a non-interface should warn ("interface can only extend interfaces").  
CASES: Inherited interface property missing on implementing type should warn.  
CASES: Compatible inherited property implementation should not warn.  
CASES: Duplicate interface property declarations/overrides should follow listed duplicate-property diagnostics.  
RISKS: API excerpt is truncated; diagnostic IDs, compiler setup, and exact warning counts are not supplied.