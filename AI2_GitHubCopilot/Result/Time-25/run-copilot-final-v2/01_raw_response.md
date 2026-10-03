TARGETS: DateTimeZone.getOffsetFromLocal(long) for ambiguous local times during DST autumn overlap
ORACLES: IANA tz rules for Europe/Moscow 2007: at 02:00 overlap the later offset +04:00 is preferred
CASES: Overlap minutes (02:00–02:59 local); gap (spring); pre/post transition; fixed offset; null
zone
RISKS: Only Moscow 2007 autumn tested; other zones/years may differ; strict/convertLocalToUTC not
covered