TARGETS: ZipArchiveEntry.isUnixSymlink(), setUnixMode(); UnixStat constants for file type/permission
bits
ORACLES: UnixStat flag values (e.g. LINK_FLAG) govern boolean output; bug: true only if exactly one
type flag set
CASES: single symlink flag → true; symlink+execute → false; symlink+regular → false; no symlink →
false; mode=0 → false
CASES: mode with only permission bits, no type bits → false; all bits except symlink → false
RISKS: UnixStat interface fields not visible; test must infer flag bitmask values from project
sources or accepted conventions
RISKS: Classpath requires Compress-42b; test cannot depend on a fixed patch version for oracles