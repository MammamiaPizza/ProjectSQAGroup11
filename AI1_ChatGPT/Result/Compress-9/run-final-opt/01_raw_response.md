TARGETS: TarArchiveOutputStream byte-count behavior during entry write, closeArchiveEntry, finish/close  
ORACLES: Existing testCount expects output/count 10240, not 76  
CASES: Normal: write a TAR entry then close/finish; verify full record/block padding is counted/emitted  
CASES: Boundary: entry sizes around record size; verify padding and EOF records affect final count  
CASES: Error: finish/close with unclosed or undersized entry only if existing API behavior is observable  
RISKS: Context lacks TarArchiveEntry construction details and exact block/record-size expectations  
