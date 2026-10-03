TARGETS: HelpFormatter.printWrapped(pw,width,text) and printWrapped(pw,width,nextLineTabStop,text)
TARGETS: findWrapPos(text,width,startPos) behavior for long unbroken text and quoted long descriptions
ORACLES: Trigger tests specify no RuntimeException/infinite-loop guard for the reported long-line inputs
ORACLES: Captured PrintWriter output is the observable result; wrapping must make progress and terminate
CASES: Unbroken word longer than width, including "looooong description" at narrow configured widths
CASES: Long description with spaces/quotes: `used if omited. Example: -e "Runs such and such"`
CASES: Boundary text at width, just over width, and continuation indentation via nextLineTabStop
RISKS: Exact line breaks are unspecified; avoid asserting undocumented formatting beyond termination/output
RISKS: Context lacks prior/fixed version and full trigger setup; derive inputs only from supplied API/report