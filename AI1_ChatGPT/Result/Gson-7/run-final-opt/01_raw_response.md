TARGETS: JsonReader.peek(), nextInt(), nextLong(), and unquoted literal/number classification in lenient mode.  
ORACLES: Trigger expectations: unquoted integer/long map keys deserialize; integer-prefixed unquoted strings remain STRING.  
CASES: Lenient unquoted numeric keys consumed by nextInt/nextLong at object-name/value boundaries.  
CASES: Arrays with unquoted literals beginning with digits; peek must not misclassify nonnumeric suffixes as numbers.  
RISKS: Exact accepted numeric grammar, overflow handling, and strict-mode behavior are not provided by this context.