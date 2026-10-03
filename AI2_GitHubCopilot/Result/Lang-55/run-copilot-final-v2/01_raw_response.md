TARGETS: getTime() after suspend/resume/stop; getSplitTime() after split/unsplit;
toString/toSplitString format.
ORACLES: suspend freezes getTime(); resume lets it progress; verify with System.nanoTime delta ±
tolerance.
CASES: start→suspend→sleep→getTime() unchanged; resume→sleep→getTime() larger; split during suspend.
ORACLES: toString() initial "0:00:00.00?"; N seconds yields digit pattern; toSplitString after
split/unsplit.
CASES: illegal: start twice, stop without start, unsplit without split, resume w/o suspend; expect
exception.
CASES: stop while suspended; getTime() constant after stop; reset zeroes; start after reset works.
RISKS: clock granularity forces tolerance; exact string format may differ; unknown internal state
guard.