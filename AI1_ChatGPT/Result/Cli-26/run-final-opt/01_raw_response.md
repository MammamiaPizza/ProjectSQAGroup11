TARGETS: OptionBuilder.create(char/String/no-arg) must reset accumulated builder state after each call.  
ORACLES: Inspect created Option properties; CLI-177 trigger says a later Option must not inherit description.  
CASES: Build withDescription then create; create a second Option without description and expect null/absent description.  
CASES: Verify reset across longOpt, required, arg/args, argName, type, and value-separator settings.  
CASES: Exercise create(char), create(String), and create() where valid; check each subsequent build starts clean.  
RISKS: Static singleton builder makes test ordering/state leakage significant.  
RISKS: API context omits Option accessors and precise invalid-create behavior; avoid unsupported error expectations.