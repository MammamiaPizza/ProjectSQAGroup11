TARGETS: printHelp overloads handling header/footer; printWrapped(width,text/tabStop); findWrapPos  
ORACLES: HelpFormatterTest::testIndentedHeaderAndFooter comparison expectation (only failure evidence)  
CASES: Header/footer containing leading indentation; verify indentation survives wrapping/output  
CASES: Header/footer at width boundary and multi-line wrapped text with configured newline  
CASES: Empty/null header or footer and widths/padding near boundaries, if supported by existing behavior  
RISKS: Output goes to stdout via printHelp; capture stream and normalize configured defaultNewLine  
RISKS: Exact expected formatting beyond trigger is unavailable; avoid assumptions about null/invalid-width behavior