TARGETS: CSVParser.iterator() state after breaking out of iteration; getRecordNumber() count after
break; getRecords() size/content after break.
ORACLES: Input CSV record count; expected record values per line; sequential iteration order;
CSVFormat header/no-header.
CASES: Break after 0,1,N‑1,N records; then continue with same or new iterator; break then call
getRecords(); break then close.
RISKS: Parser may not support re-entrant or interleaved iterators; internal lexer state not reset on
partial consumption; no spec for partial iteration.