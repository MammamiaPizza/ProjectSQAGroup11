TARGETS: TimePeriodValues.getMaxMiddleIndex() after add/update/delete
TARGETS: TimePeriodValues.recalculateBounds() internal logic for max middle
ORACLES: Expected max middle index = index of item with greatest TimePeriod.getMiddleMillisecond()
CASES: Normal: add periods with distinct middle instants; check max middle index
CASES: Boundary: all items have identical periods (equal middles); expects last-added index?
CASES: After delete: remove item with current max middle; verify index shifts to next highest
CASES: Update item value (not period) should not change max middle index
RISKS: No public spec for equal-middle tie-breaking; inferred from trigger expecting 1 not 3
RISKS: Cannot see updateBounds/recalculateBounds implementations; internal errors possible