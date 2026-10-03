TARGETS: parseFromLocalFileData parsing size, compressedSize, relativeHeaderOffset, diskStartNumber
boundaries.
TARGETS: getCentralDirectoryLength/getCentralDirectoryData consistency after central parsing (28 vs
old 16).
TARGETS: setSize/setCompressedSize/setRelativeHeaderOffset/setDiskStartNumber length contributions
and serialized output.
TARGETS: getLocalFileDataLength vs getCentralDirectoryLength for local-only sizes (16) versus full
central fields (28).
ORACLES: failure message from ZipFileTest testExcessDataInZip64ExtraField: expected length 16 but is
28.
ORACLES: round-trip getCentralDirectoryData -> parse should preserve size, compressedSize,
relativeHeaderOffset, diskStart.
CASES: absent fields, only sizes set, all four zip64 fields set, trailing excess bytes in extra
field.
CASES: buffer offset/length boundaries when fewer than required bytes are available for parsing.
RISKS: no central-directory parse method listed; infer expected fix from exception text and listed
signatures only.
RISKS: trigger archive bytes not supplied, so expected values rely on test name and failure message
alone.