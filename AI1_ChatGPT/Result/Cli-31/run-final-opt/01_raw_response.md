TARGETS: HelpFormatter usage/help rendering for Option argument names; defaultArgName and Option.getArgName  
TARGETS: Option constructors/setters and OptionBuilder.withArgName/create state transfer/reset  
ORACLES: Trigger expects usage fragment "-f <argument>" when an arg name is not explicitly set  
ORACLES: Public constants/default fields and rendered formatter output provide expected-result sources  
CASES: Option("-f", true, ...) rendered in usage with default arg name; explicit arg name overrides it  
CASES: Builder hasArg()+create("f") default name; withArgName("file") preserves explicit name  
CASES: Boundary: no-arg option must not render an argument placeholder  
RISKS: Exact whitespace/newline/wrapping depends on HelpFormatter defaults and platform line separator  
RISKS: Available context truncates formatter signatures/complete existing-test expectations