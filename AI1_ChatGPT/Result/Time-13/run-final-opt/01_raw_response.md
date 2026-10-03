TARGETS: PeriodFormatterBuilder FieldFormatter printing for appendSecondsWithMillis and negative sub-second periods.  
ORACLES: Trigger expects ISO formatter output "PT-0.008S" for the negative 8-millisecond period.  
CASES: Format negative millis-only period via ISO standard formatter; verify minus sign is retained with zero seconds.  
CASES: Format positive 8-millisecond period; expect "PT0.008S" as regression counterpart.  
CASES: Boundary zero milliseconds and negative values with nonzero seconds around millis formatting.  
RISKS: Builder internals are private; test through public formatter construction or existing ISO formatter APIs only.  
RISKS: Context provides only one expected output; avoid assuming parsing behavior or unsupported period constructors.