TARGETS: DateTimeFormatterBuilder time-zone ID parsing/printing used by formatter construction.  
ORACLES: Trigger expects parsing "America/Dawson_Creek" without malformed-format exception.  
CASES: Build date-time plus zone-ID formatter; print and parse Dawson Creek zone text.  
CASES: Parse zone IDs containing underscore; verify complete input consumption/parsed zone where exposed.  
CASES: Normal zone ID without underscore; zone ID with slash and multiple components.  
RISKS: Zone-ID matching may stop early at underscore, leaving "_Creek" as malformed suffix.  
RISKS: Available context omits full relevant API/body; derive expectations only from trigger and bug report.