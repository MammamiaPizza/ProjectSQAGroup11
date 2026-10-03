TARGETS: round(Date,int) -> modify(Calendar,int,boolean) for MINUTE field rounding
ORACLES: existing testRoundLang346 expects minute round-up to 08:09:00 from a specific mid-minute
input
CASES: round with seconds<30 vs >=30; round at 30s boundary; hour/day rollover; DST transition dates
RISKS: no round logic source; only one failure case visible; unknown other field rounding behavior;
timezone sensitive