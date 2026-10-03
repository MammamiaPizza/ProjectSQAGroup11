TARGETS: finish() on Cpio/Zip/TarArchiveOutputStream to flush central dir & trailers
ORACLES: IOException "central directory is empty" must not occur; roundtrip read == write
CASES: single-entry archive roundtrip; empty archive; multi-entry; entry with Unicode name
RISKS: Only listed public methods guaranteed; internal write behavior unknown; CPIO/TAR finish
semantics untested