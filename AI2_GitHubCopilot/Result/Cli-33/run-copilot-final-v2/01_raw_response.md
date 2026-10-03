TARGETS: printHelp(width,cmdLineSyntax,header,options,footer) indentation of header/footer.
TARGETS: printWrapped for multi-line header/footer wrapping with indentation.
ORACLES: Expected output from test assertion: header/footer lines prefixed with leftPad spaces.
CASES: normal: header="Header", footer="Footer", width=80, leftPad=3.
CASES: boundary: empty header/footer, null header/footer (expected no NPE or default behavior).
CASES: multi-line header/footer with custom nextLineTabStop to verify line continuation indentation.
RISKS: Only buggy version available; exact expected formatting rules not documented; rely on test
failure message.

We can condense into precisely the format. Each line should have label: ... and <=120 chars. We'll
output 7 lines. Enumerate.

We should mention the specific test name maybe? Not required. We'll output:

TARGETS: printHelp with header and footer parameters — indentation behavior.
TARGETS: printWrapped and renderOptions for header/footer wrapping and padding.
ORACLES: Expected output from HelpFormatterTest.testIndentedHeaderAndFooter’s assertion.
ORACLES: Visual inspection of rendered text on console to verify indentation consistency.
CASES: Normal: width=80, leftPad=3, header="MyApp", footer="(c) 2026".
CASES: Empty/null header/footer — ensure no crash and correct formatting.
CASES: Multi-line header/footer with nextLineTabStop checking line continuation indent.
RISKS: Limited to single bug report; no formal spec for HelpFormatter indent rules.

But we need exactly 10 lines at most. 8 lines maybe.

We'll output 8 lines with labels: TARGETS, TARGETS, ORACLES, CASES, CASES, CASES, RISKS, RISKS.

But "CASES" and "RISKS" misspelling? Actually label is CASES? The prompt says "CASES", "RISKS". Yes,
they wrote: "TARGETS, ORACLES, CASES, RISKS." So we must use those exact spellings.

Let's use: TARGETS, ORACLES, CASES, RISKS. Upper case? The prompt shows caps: TARGETS, ORACLES,
CASES, RISKS. We'll replicate those.

So output lines:

TARGETS: printHelp(int,String,String,Options,String) header/footer indentation.
TARGETS: printWrapped(int,int,String) used for wrapping header/footer with alignment.
ORACLES: Expected output from HelpFormatterTest.testIndentedHeaderAndFooter comparison.
CASES: Normal: width=74, leftPad=1, header="MyTool", footer="Report bugs".
CASES: Boundary: empty header/footer, null header/footer (should not throw).
CASES: Multi-line header/footer with custom nextLineTabStop to test wrap indent.
RISKS: Only buggy version accessible; formatting rules undocumented; rely on test.
RISKS: Possible ambiguity in how trailing newlines are handled in header/footer.

That's 8 lines, <=120 chars. Good.TARGETS: printHelp(int,String,String,Options,String) header/footer
indentation logic.
TARGETS: printWrapped used for wrapping multi-line header/footer with alignment.
ORACLES: Expected output from HelpFormatterTest.testIndentedHeaderAndFooter comparison.
CASES: Normal: width=74, leftPad=1, header="MyTool", footer="Report bugs".
CASES: Boundary: empty header/footer, null header/footer (should not throw).
CASES: Multi-line header/footer with custom nextLineTabStop to test wrap-indent.
RISKS: Only buggy version available; formatting rules undocumented; rely on test.
RISKS: Ambiguity in trailing-newline handling for header/footer rendering.