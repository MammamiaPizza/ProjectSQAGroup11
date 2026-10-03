TARGETS: putArchiveEntry,closeArchiveEntry,write,finish,close;inherited getBytesWritten
ORACLES: Expected = sum over entries: ceil((512+contentSize)/512)*512; after finish add 1024 EOF
CASES: Single entry "foo"(3B) => 1024; entry 512B content => 1024; 513B => 1536; 0B => 512
CASES: Multi-entry: write 3 entries (10B,200B,700B) verify cumulative blocks: each entry padded
CASES: After finish: empty archive => 2 EOF blocks=1024; 1 empty entry => 512+1024=1536
CASES: write(byte[]w,int,int) should increment bytes; closeArchiveEntry should not double-count
RISKS: getBytesWritten may only count user data ignoring headers/padding; verify after flush/close
RISKS: TarBuffer may buffer writes; ensure count reflects bytes flushed to underlying stream
RISKS: The buggy version may reset count on finish/close; test after each step to isolate