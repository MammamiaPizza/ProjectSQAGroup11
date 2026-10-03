TARGETS: ValueMarker(double, Paint, Stroke) constructor; equals(Object); getValue/setValue.
ORACLES: passed Paint/Stroke must be preserved and observable via inherited getPaint/getStroke.
CASES: construct with blue Paint + custom Stroke, assert getPaint returns blue, not red.
CASES: equals for identical vs differing value, paint, and stroke combinations.
CASES: setValue on NaN, +/-Infinity, negative, zero; equals before/after setValue.
CASES: null Paint/Stroke constructor input and inherited defaults.
RISKS: Marker superclass defaults and exact expected colors not supplied in context.