TARGETS: CSVPrinter.print(null) when nullString is set; CSVPrinter.print(null) when nullString is
null.
ORACLES: MYSQL format default nullString is "\N"; custom nullString "NULL" sets printed
representation.
CASES: print null with MySQL format (expect "\N"), with nullString="NULL" (expect "NULL"), with
nullString=null (expect "" or "null"?).
RISKS: If format.getNullString() returns null, code may NPE or output "null" instead of
empty/configured value.
ORACLES: Test expects null prints literal nullString value; no implicit replacement.