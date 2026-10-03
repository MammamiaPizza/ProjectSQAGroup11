TARGETS: BaseSettings.withDateFormat(DateFormat), with(TimeZone), getDateFormat(), getTimeZone()
ORACLES: Trigger expects America/Los_Angeles retained rather than GMT after date-format configuration.
CASES: Configure a DateFormat with America/Los_Angeles; assert resulting settings/date format timezone.
CASES: Apply explicit with(TimeZone) before/after withDateFormat; verify returned settings expose intended timezone.
CASES: DateFormat with GMT and non-GMT timezones; verify timezone propagation is not silently reset.
RISKS: DateFormat mutability/cloning may affect identity-based assertions; assert timezone values.
RISKS: Constructor/setup API is not provided; tests may need existing project fixtures to create BaseSettings.