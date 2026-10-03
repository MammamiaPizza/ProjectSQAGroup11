TARGETS: getNextCPIOEntry(); old ASCII parsing via readOldAsciiEntry/readAsciiLong(mode, 8).  
ORACLES: Trigger archive must no longer throw "Unknown mode. Full: 1a4 Masked: 0".  
ORACLES: Entry metadata/result expectations must come from existing CpioArchiveInputStreamTest fixture.  
CASES: Read successive entries from the Redline RPM-created CPIO archive.  
CASES: Old ASCII mode value 01a4 (permission bits without a file-type mask).  
CASES: Verify normal entry iteration and payload reading after the affected header.  
RISKS: Fixture contents and exact expected entry fields are not provided in this context.