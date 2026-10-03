TARGETS: Timer(long) constructor validation for negative duration; public isCounting/start may also
be affected indirectly.
ORACLES: Expect FriendlyReminderException when durationMillis < 0; no exception for duration >= 0;
exception message should mention "negative".
CASES: Negative: -1, -100, Long.MIN_VALUE; boundary zero; positive small/large; verify no throw for
0, 1, Long.MAX_VALUE.
RISKS: FriendlyReminderException may need specific import
(org.mockito.exceptions.misusing.FriendlyReminderException); exact message wording uncertain; test
will fail (no exception) in buggy version, pass after fix.