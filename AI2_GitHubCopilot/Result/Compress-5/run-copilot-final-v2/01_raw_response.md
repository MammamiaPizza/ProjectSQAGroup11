TARGETS: ZipArchiveInputStream.read on truncated entry after entry start
TARGETS: ZipArchiveInputStream.getNextZipEntry for incomplete multi-volume zip
ORACLES: read must throw IOException when entry truncated (COMPRESS-87)
ORACLES: getNextZipEntry must not silently ignore missing subsequent volume
CASES: read after partial entry: truncation at first byte, mid-data, last byte
CASES: multi-volume zip with only first disk; stream from missing volume
CASES: normal complete entry read should succeed without exception
RISKS: multi-volume test files may be absent; define reasonable truncation stub
RISKS: bug may only manifest when Inflater/CRC32 processing certain data