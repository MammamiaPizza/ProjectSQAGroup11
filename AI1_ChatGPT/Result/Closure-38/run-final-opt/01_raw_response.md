TARGETS: CodeConsumer.isNegativeZero(double); CodePrinter spacing when emitting minus before -0.0  
ORACLES: Trigger expects printed output "x- -0.0" (space separates consecutive minus tokens)  
CASES: isNegativeZero(-0.0); emission after prior '-' followed by negative-zero numeric literal  
CASES: Contrast normal negative/nonnegative zero only where accessible through existing printer tests  
RISKS: CodeConsumer is abstract/package-private; test via same-package concrete consumer or CodePrinter path  
RISKS: Context exposes no full append/printing API or formatting rules beyond the trigger failure