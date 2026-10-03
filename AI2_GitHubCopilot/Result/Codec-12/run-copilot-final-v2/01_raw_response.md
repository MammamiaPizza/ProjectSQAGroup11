TARGETS:read(), read(byte[],int,int), skip(long), available(), markSupported()
ORACLES:FilterInputStream contract: read returns decoded byte/-1; skip returns actual skipped
decoded bytes
ORACLES:available returns remaining decoded bytes (not raw buffer); markSupported returns false
CASES:skip to end returns -1; skip big block; read-after-skip correctness; skip 0; available
before/after reads
CASES:skip past end of stream; multiple skip/read interleave; read after all data consumed
CASES:skip with negative argument (expect IllegalArgumentException); read on closed stream;
offset/length checks
RISKS:No source for internal buffer logic; must infer from BaseNCodec API behavior and test
assertions