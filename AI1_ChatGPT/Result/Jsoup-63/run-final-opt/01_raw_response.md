TARGETS: HtmlTreeBuilder token processing and Tokeniser self-closing-flag acknowledgement/error reporting.  
ORACLES: Trigger assertions define exact ParseError positions/messages and error counts.  
CASES: Self-closing non-void tag reports “Tag cannot be self closing; not a void tag” at position 18.  
CASES: Self-closing void tag produces zero parse errors.  
CASES: Error tracking requested reports non-void self-closing error at position 50.  
RISKS: Context omits source bodies and public parser/error-list APIs; derive expectations only from listed triggers.