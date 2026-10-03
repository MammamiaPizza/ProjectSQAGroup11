TARGETS: Soundex.encode(String), soundex(String), and private getMappingCode H/W handling.  
ORACLES: Existing trigger asserts Soundex output Y330; buggy result is Y300.  
CASES: Exercise letters separated by H and W, especially mappings involving code 3.  
CASES: Compare normal adjacent-letter encoding with H/W-separated-letter behavior.  
CASES: Verify four-character output formatting and zero padding around affected codes.  
RISKS: Trigger input is not provided; derive exact inputs only from available tests/source context.