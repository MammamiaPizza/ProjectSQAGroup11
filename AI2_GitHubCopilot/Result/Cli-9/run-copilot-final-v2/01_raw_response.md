TARGETS: Parser.checkRequiredOptions() exception message building
ORACLES: JUnit's expected exception message with ", " as option separator
CASES: 1 missing option → message without separator; 2+ missing → ", "-separated
RISKS: Message format (language, prefix) may depend on localization or JVM