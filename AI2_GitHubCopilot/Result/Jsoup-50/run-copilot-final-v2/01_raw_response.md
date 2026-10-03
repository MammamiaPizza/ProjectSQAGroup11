TARGETS  : DataUtil.parseByteData, readFileToByteBuffer, load(File,*)
ORACLES : parsed Document text must not start with BOM; charset derived from BOM when present
ORACLES : load yields Document with expected body text (trimmed) and correct charset
CASES   : UTF-8 BOM, UTF-16LE BOM, UTF-16BE BOM files → no leading \uFEFF in output
CASES   : UTF-8 BOM with charset in meta; UTF-8 without BOM; empty file; large file
CASES   : File with BOM but no explicit charset; File with BOM + conflicting meta charset
RISKS   : BOM removal may corrupt first character; charset detection may fail for UTF-16
RISKS   : Only file-based loading triggering; InputStream path may already work differently