TARGETS: TypeValidator diagnostic reporting for missing/undefined properties in interface/type checks.  
ORACLES: Trigger expects "...p never defined on C2", not "...p never defined on C3.c2_".  
CASES: Reproduce Issue1047 nested property access whose receiver type should remain C2.  
CASES: Check diagnostic property name and owner-type rendering for the reported undefined property.  
RISKS: Only TypeValidator private methods are listed; exercise behavior through TypeCheckTest/compiler flow.  
RISKS: No API/spec beyond trigger failure is provided; avoid assuming unrelated type-check diagnostics.