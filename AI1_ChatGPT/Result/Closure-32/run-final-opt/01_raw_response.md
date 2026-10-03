TARGETS: JsDocInfoParser comment extraction for @license and @preserve textual blocks.  
ORACLES: Existing trigger assertions: preserved text expected to contain " Foo"; IntegrationTest expects "/*".  
CASES: Single-line @license/@preserve comments with ASCII text and leading/trailing whitespace.  
CASES: Multiline textual blocks; verify delimiters/newlines and comment text are retained as trigger tests expect.  
CASES: Issue701 integration input exercising emitted preserved license comment formatting.  
RISKS: Extraction helpers are private; test via parser/compiler behavior and existing public test harness only.  
RISKS: Context omits constructor/public parse entry points and full expected strings; avoid ungrounded API assertions.