TARGETS: StrBuilder.appendFixedWidthPadLeft(Object,int,char);
appendFixedWidthPadRight(Object,int,char) null path.
TARGETS: Behavior when obj is null relative to getNullText()/setNullText(String); default nullText
should pad as empty.
ORACLES: Trigger tests testLang412Left/Right assert exact fixed-width padded output (e.g. 10 pad
chars) without NPE.
ORACLES: Null object contributes zero chars, so width is filled entirely by padChar; source:
appendNull/pad semantics.
CASES: LEFT null obj, width 10, ''; RIGHT null obj, width 10, ''.
CASES: width 0; width negative; width equal to string length; width greater than current builder
size.
CASES: setNullText(null) then pad null; setNullText("N") pads around the configured non-null
nullText.
RISKS: Available API list is truncated and omits the pad methods and default nullText
initialization.
RISKS: Exact expected output for non-null nullText not provided; verify only against the two trigger
assertions.