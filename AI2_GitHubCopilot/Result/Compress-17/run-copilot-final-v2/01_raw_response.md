TARGETS: TarUtils.parseOctal(byte[],offset,length) — parses octal field; includes negative via
leading 0xff byte
TARGETS: TarUtils.parseName(byte[],offset,length) — extracts name string, handles null-termination
and spaces
TARGETS: TarUtils.verifyCheckSum(byte[]) — validates tar header checksum, likely uses parseOctal
internally
ORACLES: POSIX ustar / GNU tar specification: octal values are zero-padded, names are
null-terminated ASCII
ORACLES: Known-good tar entry byte sequences; compare decoding results to expected numeric/string
values
CASES: parseOctal: leading 0xff for negative numbers, all zeros, max 12-digit, invalid oct digit
throw
CASES: parseOctal: buffer exactly at offset+length, offset beyond buffer, length=0 (edge)
CASES: parseName: 100-char name with trailing spaces, name with embedded null byte, empty input
CASES: verifyCheckSum: correct sum, corrupted sum byte (non-octal), negative checksum from 0xff
padding
RISKS: Trigger test’s tar entry bytes unknown; reproduce suspect oct/name parsing via generic edge
cases