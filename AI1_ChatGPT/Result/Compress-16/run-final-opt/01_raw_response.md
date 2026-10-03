TARGETS: ArchiveStreamFactory.createArchiveInputStream(InputStream) auto-detection behavior.  
ORACLES: Trigger says AIFF input must not create an archive input stream.  
CASES: AIFF-signature stream; expect failure rather than a TAR/archive stream.  
CASES: Valid archive signatures only if existing tests establish their expected stream type.  
RISKS: TAR detection may falsely accept AIFF bytes due to weak signature matching.  
RISKS: Context exposes only auto-detect overload; exception type and full format rules are unspecified.