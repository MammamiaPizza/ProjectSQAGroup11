TARGETS: CSVPrinter.printRecord(values) when CSVFormat has header; println; printRecords.
ORACLES: Expected header row is columns joined by delimiter, then recordSeparator, no trailing
delimiter.
CASES: Normal: print records after header -> headerRow\nrecord1\n...; Boundary: header set, print
zero records -> headerRow\n; header set, print empty record -> ?; header set, print null values.
RISKS: Header printed via first printRecord call only; bug could be missing newline after header or
extra delimiter at end.

But need to be within 120 char per line.

Let's make concise lines:

TARGETS: CSVPrinter.printRecord(Iterable), CSVPrinter.printRecord(Object...) when format has header.
CSVFormat.withHeader(String...) sets header columns.
ORACLES: Output from StringWriter after close(); header row = headerColumns joined by delim,
terminated by recordSeparator.
CASES: Header+1 record -> headerRow\nrecordRow; header+0 records -> headerRow only; no header set ->
no header row; header null values -> empty fields.
RISKS: CSVPrinter may not output header at all or output duplicates; recordSeparator may be missing
after header; trailing delimiter may cause mismatch.

That's 4 TARGETS, 4 ORACLES, 4 CASES, 3 RISKS = 15 lines, too many. Need at most 10 lines total,
each <=120 characters.

We can combine some.

TARGETS: CSVPrinter printRecord (both overloads), println, close; when CSVFormat header is present.
ORACLES: Expected string from StringWriter after close; header line = headerColumns delimited,
terminated by recordSeparator; no extra delimiters.
CASES: Normal: header+records -> headerRow\nrecordRow(s); boundary: header present but no records
printed -> headerRow; error: null values in header; records with special chars quoted.
RISKS: Bug may cause missing header row or duplicate header; unclear if header printed on
constructor or first printRecord; CSVFormat header detection may interfere with record count.

That's still 2+3+4+3 = 12. Could combine some cases.

Alternatively, provide fewer lines: maybe 3 TARGETS, 2 ORACLES, 3 CASES, 2 RISKS = 10.

Let's do:

TARGETS:
TARGETS: CSVPrinter.printRecord(values) [Iterable and varargs] when CSVFormat with header set;
CSVPrinter.println(), close()
ORACLES: Output via appendable.toString(); headerLine = columns+recordSeparator, no trailing delim;
row format consistent with format config
CASES: header+1 record yields expected output; header+zero records yields headerLine only; header
with empty column name results in empty field between delimiters
RISKS: header may not be printed at all; may be printed twice if printRecord called multiple times
without newRecord transition; confusable with CSVFormat modifications

I'll produce exactly 10 lines.

Implementation:TARGETS: CSVPrinter.printRecord(Iterable) / printRecord(Object...) when format has
header columns; println() adds recordSeparator; close() flushes output.
TARGETS: Header detection logic in CSVPrinter using format.getHeader().
ORACLES: StringWriter.toString() after close() should contain header row: columns joined by
delimiter, terminated by recordSeparator.
ORACLES: No trailing delimiter in header row; each row (header + records) followed by
recordSeparator; no blank lines unless empty record.
CASES: (normal) header+1 record -> "C1,C2,C3\nV1,V2,V3\n"; (boundary) header+0 records ->
"C1,C2,C3\n"; (boundary) header with null column -> "C1,\nV1,V2\n".
RISKS: Bug may cause header to not be printed at all, or printed after first record, or duplicate
header on subsequent printRecord calls.
RISKS: Format with header but no call to printRecord may still produce header?; recordSeparator
after last record but no final newline may misalign expected.