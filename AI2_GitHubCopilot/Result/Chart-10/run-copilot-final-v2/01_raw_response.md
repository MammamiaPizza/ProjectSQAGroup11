TARGETS: generateToolTipFragment(String) must HTML-escape double quotes in title attribute to "
ORACLES: Trigger test expects title="Series ["A"], 100.0" alt="", actual output has raw " chars
CASES: Normal text without special chars; text with single " and multiple "; single-quote chars
CASES: Text with &, <, > (common HTML entities); empty string ""; null tooltip text (NPE?)
CASES: Long tooltip strings; newlines or tabs; tooltips containing existing " (double-escaping risk)
RISKS: Only double-quote escape verified; other HTML-entity needs unknown; null-handling behavior
unclear