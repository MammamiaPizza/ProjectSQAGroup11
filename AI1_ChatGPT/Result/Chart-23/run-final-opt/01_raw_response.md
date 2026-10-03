TARGETS: MinMaxCategoryRenderer equality after state changes via draw-lines, paint, stroke, and icon setters.
ORACLES: Existing failing testEquals is the expected-result source; compare equivalent instances and changed state.
CASES: Two default instances should compare equal if existing test defines this baseline.
CASES: Toggle setDrawLines(boolean) on one instance; verify equality changes as specified by testEquals.
CASES: Change group paint/stroke and object/max/min icons independently; check equality impact.
RISKS: Icons, Paint, and Stroke may be null unless setters validate; API contracts are not shown.
RISKS: No source body or full test assertions are provided; exact equality fields cannot be assumed.