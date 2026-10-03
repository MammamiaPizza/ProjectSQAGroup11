TARGETS: XYSeries.addOrUpdate(Number x, Number y), especially with autoSort=false.
CASES: Add first item via addOrUpdate() to a non-auto-sorted series; must not use index -1.
ORACLES: Trigger test testBug1955483 and public getItemCount(), getX(), getY() results.
CASES: Normal addOrUpdate insertion; repeat same x to exercise update-versus-insert behavior.
CASES: Boundary empty series and single-item series, matching failing Size: 1 / Index: -1 context.
RISKS: Duplicate-x handling depends on constructor allowDuplicateXValues configuration.
RISKS: No fixed expected values beyond available API, trigger failure, and documented constructor options.