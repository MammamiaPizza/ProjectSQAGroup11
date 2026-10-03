TARGETS: ExtendedProperties.interpolate(String), interpolateHelper(String,List)
TARGETS: ExtendedProperties.escape(String), unescape(String) – static helpers
ORACLES: Backslash in property value must survive load→getString round-trip unchanged
ORACLES: Properties-format spec: "\" in .properties file = literal "" in value
CASES: Value with Windows UNC path like \192.168.1.91\test
CASES: Value with escaped "\n", "\t" and mixed literal+escape backslashes
CASES: Interpolated ${key} where referenced value contains backslashes
CASES: Empty string, single char, no escapes, multiple consecutive backslashes
RISKS: Interaction between Java Properties loader escaping and ExtendedProperties own unescape()
RISKS: Only modified source is ExtendedProperties; cannot inspect PropertiesReader or
PropertiesTokenizer internals