TARGETS: ZipArchiveInputStream.getNextZipEntry(), read(byte[],int,int), closeEntry(), readFully(), fill()  
ORACLES: Trigger expects truncated-entry reads not to succeed or silently return normal data/EOF  
CASES: Read entries from the 7-Zip multi-volume archive through a stream until truncation is reached  
CASES: After obtaining the truncated entry, attempt read with a nonempty buffer and verify failure is reported  
CASES: Exercise sequential reads across the available portion before the truncated boundary  
RISKS: Distinguish legitimate end-of-entry from premature underlying-stream exhaustion  
RISKS: Context lacks archive fixture bytes and exact expected exception type/message  
