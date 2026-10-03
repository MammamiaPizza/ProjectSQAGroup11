TARGETS: appendSeconds() parsing of long-digit second values (>9 digits)
ORACLES: parsePeriod("PT1003199059S").getSeconds()==1003199059, no exception
CASES: parse PT1S, PT999999999S, PT1003199059S; boundary: 10-digit int, 0S
RISKS: default maximumParsedDigits not visible; sign rejection unknown; only restore no-exception
behavior