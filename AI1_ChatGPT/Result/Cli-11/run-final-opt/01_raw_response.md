TARGETS: HelpFormatter usage rendering for an option with an empty argument name  
ORACLES: Trigger expectation: usage output is "usage: app -f" with no argument placeholder  
CASES: Option requiring an argument whose argName is empty; verify printHelp usage line  
CASES: Compare empty argName behavior against default/nonempty argument-name usage formatting  
RISKS: Available signatures omit Option/Options constructors and detailed printHelp overloads  
RISKS: Do not infer output beyond the reported usage prefix, command "app", and "-f" result