TARGETS: createNumber(String) with suffix chars (L,l,F,f,D,d) and hex/octal prefixes; isAllZeros
indirectly via stripped mantissa.
ORACLES: returning a correct Number subclass or throwing NumberFormatException; never throwing
IndexOutOfBounds or other runtime errors; spec from Javadoc.
CASES: empty(""), single suffix ("L","l","F","f"), signed suffix ("- L","+ L"), hex suffix ("0x
L","0x0L","-0x0L"), valid long with "l" ("1l","0x1l"), floats with "f"/"d" ("1.0f","0.0D"), invalid
strings ("--1","0x G","0x.0").
CASES: normal: "123","-45","0x1A","0.0","-0","3.14","1e3","1e-3","-0x0","1L","0L","-0x0L".
RISKS: Only testLang457 trigger and exception trace known; no access to buggy createNumber body; fix
per issue: after stripping suffix, mantissa may become empty causing charAt(0) crash; assume
similar risks for other prefix+suffix combinations.