TARGETS: Parser.parse(Options,String[]), setOptions, getRequiredOptions, processOption, checkRequiredOptions  
ORACLES: MissingOptionException must be thrown for absent required options on each parse  
CASES: Reuse one Parser and Options instance; parse twice with required option absent both times  
CASES: Parse required option present, then absent on a subsequent parse using the same parser/options  
CASES: Independent parses should retain normal required-option validation behavior  
RISKS: Mutable required-options state may be consumed during first parse and not reset before reuse  
RISKS: Context lacks concrete Parser subclass, option setup details, and full parse semantics