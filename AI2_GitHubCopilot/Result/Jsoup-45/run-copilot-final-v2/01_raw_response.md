TARGETS: process(Token) end-tag handling for td, th; isElementInQueue; inSpecificScope;
clearStackToContext; replaceInQueue; formattingElements list.
ORACLES: testReinsertionModeForThCelss expected 1; node count after parsing; final HTML structure;
expected element positions.
CASES: <table><tr><td><b>text</td> (missing close); nested <th><i></th>; empty <td></td>;
consecutive cells with formatting; table inside td.
RISKS: exact test input unknown; failure message only; may rely on internal queue order; no
available spec excerpts.