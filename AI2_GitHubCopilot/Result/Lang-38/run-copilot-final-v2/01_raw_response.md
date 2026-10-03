TARGETS: FastDateFormat.format(Calendar) — must use Calendar's timezone, not instance timezone
ORACLES: compare format(Calendar) output with SimpleDateFormat of same pattern and Calendar's
timezone
CASES: Calendar with UTC, America/Chicago, Asia/Kolkata, DST-transition date
CASES: Calendar with null → expect NullPointerException
CASES: same millis value, two different Calendar timezones → different formatted strings
CASES: FastDateFormat.getInstance(pattern) without timezone, feed Calendar with non-default zone
RISKS: test must explicitly set Calendar's timezone to insulate from system default
RISKS: oracle SimpleDateFormat is not thread-safe; confine to test method
TARGETS: format(long millis) may not be affected; verify it uses instance timezone as before