TARGETS: Parser.parse/processOption/checkRequiredOptions required-option validation and MissingOptionException messages  
ORACLES: Trigger assertions define exact messages: "Missing required option: f" and "Missing required options: fx"  
CASES: Parse with one missing required option; assert MissingOptionException message includes singular prefix and option key  
CASES: Parse with multiple missing required options; assert message includes plural prefix and required keys  
RISKS: Required-option order/content follows Options/Parser state; avoid assuming ordering beyond trigger evidence  
RISKS: Context omits concrete parser subclass, option setup, and full exception API/other Parser behavior