TARGETS: putArchiveEntry, closeArchiveEntry, write with POSIX long-file mode and non-ASCII directory names  
ORACLES: Trigger expects no IOException for generated Pax header './PaxHeaders.X/fvv/' size 0  
CASES: Non-ASCII directory entry, setLongFileMode(LONGFILE_POSIX), then put/close/finish  
CASES: Verify zero-size directory/Pax entries accept no payload and archive output completes  
CASES: Compare payload write enforcement: writing 15 bytes to declared size 0 must fail when applicable  
RISKS: Only failure trace is available; exact generated header naming/content and archive-byte oracle are unspecified