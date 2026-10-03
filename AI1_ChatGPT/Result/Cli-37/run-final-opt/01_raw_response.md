TARGETS: DefaultParser.parse overloads; short option argument consumption and option recognition.  
ORACLES: Bug trigger expects later "-last" parsed as an option, not value of preceding short option.  
CASES: Parse adjacent short options where first takes no argument and second is "-last".  
CASES: Verify resulting CommandLine reports both options; first has no value and "-last" is not its value.  
CASES: Cover short option with required argument followed by a non-option value.  
CASES: Cover short option with required argument followed by another option; expect parser argument/error behavior.  
RISKS: Exact Options/Option setup and expected exception semantics are not supplied by the context.