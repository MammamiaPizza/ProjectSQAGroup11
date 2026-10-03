TARGETS: HttpConnection.Base.header(String,String), especially repeated header names with differing case.  
ORACLES: Trigger specifies "Cache-Control": "no-cache" then "no-store" yields "no-cache, no-store".  
CASES: Add same-name headers twice; verify header(name) combines values with comma and space.  
CASES: Repeat using case variants to verify case-insensitive existing-header lookup/combination.  
RISKS: Public headers() map may expose one normalized/original key; assert only documented header lookup behavior.  
RISKS: Context lacks implementation details for null, empty, removal, and unrelated header semantics.