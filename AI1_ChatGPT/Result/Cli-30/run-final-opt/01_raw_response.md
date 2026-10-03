TARGETS: DefaultParser.parse(..., Properties, ...), handleProperties; Parser.parse(..., Properties, ...), processProperties  
ORACLES: Existing property-option trigger tests define expected parsing and exception behavior  
CASES: Properties selecting one option in an OptionGroup should not cause AlreadySelectedException  
CASES: Properties containing an unexpected option must not cause NullPointerException  
CASES: Property processing across Basic, Default, Gnu, and Posix parser test coverage  
RISKS: Required-option/group selection bookkeeping may differ between command arguments and properties  
RISKS: Context omits exact Properties keys/values and expected outcome for unexpected property options