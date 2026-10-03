TARGETS: StandardToolTipTagFragmentGenerator.generateToolTipFragment(String)
ORACLES: Trigger expects title="Series [&quot;A&quot;], 100.0" alt="" for quoted input text
CASES: Normal text produces a title attribute fragment with empty alt attribute
CASES: Embedded double quotes are HTML-escaped as &quot; in generated title text
CASES: Boundary/error behavior for null or other HTML-sensitive characters is not specified
RISKS: Only one quote-escaping failure is evidenced; avoid assuming escaping rules for other characters