TARGETS: TarUtils.verifyCheckSum and computeCheckSum behavior used for TAR signature detection.  
ORACLES: Checksum acceptance is implied by DetectArchiverTestCase.testCOMPRESS335 trigger.  
CASES: Valid TAR header with checksum field bytes treated as spaces during checksum calculation.  
CASES: Header containing bytes >=0x80 with a stored checksum matching unsigned-byte summation.  
CASES: Header containing bytes >=0x80 with a stored checksum matching signed-byte summation.  
CASES: Mismatched checksum must be rejected; malformed/non-octal checksum field is an error boundary.  
RISKS: TAR checksum compatibility may require accepting both signed and unsigned checksum conventions.  
RISKS: No fixture/header layout details beyond the trigger and TarUtils API are provided.