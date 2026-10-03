TARGETS: ArchiveStreamFactory.createArchiveInputStream(InputStream) TAR detection path
ORACLES: Must throw ArchiveException for AIFF ("FORM") byte header; valid TAR magic returns
TarArchiveInputStream
CASES: AIFF "FORM..." header → throws; true ustar TAR → stream; WAV/IFF "FORM" → throw;
shortened/zero-length stream; truncated TAR
RISKS: Overly tight TAR check may reject valid GNU/POSIX TARs; must keep ZIP, AR, CPIO detection
unchanged