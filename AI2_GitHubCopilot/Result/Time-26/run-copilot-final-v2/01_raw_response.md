TARGETS: ZonedChronology.localToUTC, convertField, LenientDateTimeField.set,
DateTimeZone.convertLocalToUTC, getOffsetFromLocal

ORACLES: Expected offsets from IANA tz rules (e.g., Paris fall-back 2010-10-31 +02:00→+01:00); Joda
DateTime.with* retains original offset in DST overlap

CASES: Normal: set fields far from DST → offset unchanged. Boundary: set at DST start (gap) & end
(overlap). Error: spring-forward skip → lenient adjustment. fall-back: preserve original offset.
Mock zone transitions withBug2182444

RISKS: LenientDateTimeField.set may convert via UTC ignoring original offset hint; tests sensitive
to zone + DST transition instant selection; must match internal lenient rounding