TARGETS: HtmlTreeBuilder.process and initialiseParse during HTML parsing with template/table tokens  
TARGETS: HtmlTreeBuilderState.process and table-related state transitions for template placement  
ORACLES: HtmlParserTest::testTemplateInsideTable assertions are the available expected-result source  
CASES: Parse a template inside a table; verify the trigger test’s resulting parse tree behavior  
CASES: Exercise template start/end tokens while table context remains active  
CASES: Include surrounding table structure to cover table-state entry and exit boundaries  
RISKS: State-stack or active-formatting-element handling may misplace nodes or select a wrong mode  
RISKS: No test body, input HTML, or asserted DOM details are provided; derive no additional expectations