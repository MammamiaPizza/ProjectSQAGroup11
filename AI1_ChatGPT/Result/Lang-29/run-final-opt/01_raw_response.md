TARGETS: private getJavaVersionAsInt() and version parsing helpers toJavaVersionInt/IntArray/toVersionInt.
ORACLES: Trigger test expects Java version-as-int result 0, not floating result 0.0.
CASES: Current runtime Java version yielding no recognized integer version should produce int 0.
CASES: Exercise parseable dotted versions and empty/malformed version strings through exposed package helpers where available.
RISKS: getJavaVersionAsInt is private; test access may require reflection.
RISKS: JAVA_VERSION is initialized from environment; property-dependent tests may be runtime-sensitive.
RISKS: API listing truncation limits visibility into exact parsing rules and accepted version formats.