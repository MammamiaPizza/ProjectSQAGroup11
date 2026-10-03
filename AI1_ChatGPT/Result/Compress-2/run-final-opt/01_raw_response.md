TARGETS: ArArchiveInputStream.getNextArEntry(), getNextEntry(), read(byte[],int,int), and entry sequencing  
ORACLES: ArTestCase::testArDelete expects exactly 1 entry; use its archive fixture as expected-result source  
CASES: Iterate getNextArEntry through the delete-test archive and count non-null entries  
CASES: Verify getNextEntry delegates consistently with getNextArEntry on the same input  
CASES: Read each returned entry fully, then request the next entry  
RISKS: Entry-boundary reads may consume bytes needed for the following header  
RISKS: Context lacks the delete archive bytes and expected entry metadata beyond count=1