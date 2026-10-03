TARGETS: encodeBase64(byte[],boolean,boolean,int) — accurate output-size estimation and
maxResultSize enforcement
ORACLES: expected encoding length = 4*ceil(inputLen/3) (no chunk); no throw when output ≤
maxResultSize; IllegalArgumentException otherwise
CASES: n=4,max=4(expected 8)→no throw; n=4,max=3→throw; n=1,max=4→no throw; n=1,max=3→throw; empty
max=0,1; large n at boundary; urlSafe variant
RISKS: Bug only in static method; ensure fix does not regress chunked/urlSafe modes; lineSeparator
must not affect maxResultSize calculation