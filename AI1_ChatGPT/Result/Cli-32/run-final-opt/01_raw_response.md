TARGETS: protected findWrapPos(String,int,int); public printWrapped(PrintWriter,int,int,String).
ORACLES: Trigger assertions: findWrapPos wrap position 3 must return 3, not 4.
ORACLES: printWrapped/rendered wrapping must not throw StringIndexOutOfBoundsException.
CASES: Text with a word boundary exactly at width; verify returned wrap index uses that boundary.
CASES: Word-cut input that previously indexes past text end during printWrapped wrapping.
CASES: Normal whitespace-separated text, varying startPos and width around a boundary.
CASES: Boundary text shorter than width and text with no prior whitespace.
RISKS: findWrapPos is protected; test access may require same package or subclass.
RISKS: Context lacks exact trigger inputs and full expected wrapped output.