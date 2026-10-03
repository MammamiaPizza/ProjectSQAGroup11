TARGETS: ArchiveStreamFactory encoding constructor/getEntryEncoding and input/output stream creation paths.
TARGETS: CPIO, DUMP, TAR, ZIP stream constructors/entry-name encoding behavior.
ORACLES: Triggering ArchiveStreamFactory encoding tests; archive entry names after stream round-trip/read.
CASES: Factory with explicit encoding; create streams for supported archive formats and non-ASCII entry names.
CASES: Autodetection from archive bytes with configured encoding; output then input preserving entry names.
CASES: Default/no encoding versus explicit encoding; CPIO/TAR/ZIP boundary coverage.
RISKS: Available signatures omit factory overloads and complete stream APIs; avoid assuming unsupported formats/semantics.
