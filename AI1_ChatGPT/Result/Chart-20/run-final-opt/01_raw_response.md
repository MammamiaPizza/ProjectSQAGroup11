TARGETS: ValueMarker(double), ValueMarker(double, Paint, Stroke), getValue(), setValue(), equals(Object).
ORACLES: Trigger test1808376 expects Color.BLUE where actual was Color.RED.
CASES: Construct with explicit Color.BLUE and Stroke; verify inherited paint is BLUE.
CASES: Construct default marker; verify value round-trips via getValue/setValue.
CASES: equals for same/different value and paint configurations.
RISKS: Marker inherited paint access/API is not listed; inspect existing tests for valid oracle usage.
RISKS: Bug report is UNKNOWN; only trigger establishes explicit constructor paint behavior.