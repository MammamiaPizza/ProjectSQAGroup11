TARGETS: DateTimeZone.adjustOffset(long,boolean) near DST gap/overlap boundaries
TARGETS: DateTimeZone.getOffsetFromLocal(long) which invokes adjustOffset internally
ORACLES: DST rule: Brazil 2012 starts 02/26 00:00, offset shifts -02:00→-03:00
ORACLES: adjustOffset true moves past gap → new offset; false stays → old offset
ORACLES: Overlap transitions: true=earlier(summer) offset, false=later(winter) offset
CASES: Gap: zone=America/Sao_Paulo, local=2012-02-25T23:15, earlierOrLater=true⇒-03:00
CASES: Gap: same zone/time, earlierOrLater=false⇒-02:00
CASES: Overlap: zone with fall-back, local in repeated hour, test both boolean values
CASES: No transition: instant far from cutover returns getOffset(instant) unchanged
RISKS: Expected offsets depend on IANA db version; may differ from JDK's java.util.TimeZone.