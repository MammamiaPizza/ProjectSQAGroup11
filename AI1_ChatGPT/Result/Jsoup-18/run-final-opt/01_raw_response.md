TARGETS: CharacterReader carriage-return normalization, end-of-input consumption, and parser-visible whitespace handling  
ORACLES: Trigger assertions define expected CR-as-LF behavior and full remaining-text consumption  
ORACLES: Parser trigger expects whitespace/newline between attributes, without creating an empty attribute  
CASES: Input "one\r two" should expose normalized newline behavior matching the carriage-return trigger  
CASES: Consume remaining text "one two three" and verify the final character is retained  
CASES: Parse a tag with newline/whitespace between href and id attributes  
RISKS: EOF/buffer-boundary handling may drop trailing characters or mishandle CR during scanning  
RISKS: Available CharacterReader signatures are incomplete; avoid assuming unlisted callable APIs