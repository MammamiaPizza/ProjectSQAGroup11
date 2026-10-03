TARGETS: CSVFormat#printAndEscape(CharSequence,int,int,Appendable) – escaping when escape set but
quote is null.
TARGETS: CSVFormat#print(Object,Appendable,boolean) and withEscape/withQuote – state that determines
quoting.
ORACLES: CSVPrinterTest failures: withEscape('\').withQuote(null) must print escape char without
surrounding quotes.
CASES: Value containing escape char (quote=null); value=only escape char; empty value; escape at
start/mid/end.
CASES: Escape=null and quote=null; escape char equals delimiter; double-escape when quote is
present.
RISKS: Oracle derived only from failure messages; exact expected output may need full test code.
RISKS: QuoteMode values (e.g., NONE, MINIMAL) could interact with null-quote escaping behavior.