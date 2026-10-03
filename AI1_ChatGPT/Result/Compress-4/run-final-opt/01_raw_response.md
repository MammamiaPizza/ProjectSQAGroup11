TARGETS: ZipArchiveOutputStream finish/close must emit a readable central directory for produced ZIP/JAR archives.  
TARGETS: CpioArchiveOutputStream, TarArchiveOutputStream finish/close; ChangeSetPerformer.perform archive completion.  
ORACLES: Read generated ZIP/JAR with archive input APIs; no "central directory is empty" IOException.  
ORACLES: Existing UTF8ZipFilesTest and JarArchiveOutputStreamTest trigger expectations define round-trip behavior.  
CASES: ZIP/JAR with one entry, finish then close; verify entry is discoverable and content/name round-trips.  
CASES: ASCII, UTF-8, and CP437 names with implicit/explicit Unicode extra-field policies and EFS variants.  
CASES: Empty-output archive finalization versus archive containing an entry; seekable and stream outputs if available.  
RISKS: Signatures are truncated; exact ZIP encoding-policy setters and reader assertions are not fully provided.