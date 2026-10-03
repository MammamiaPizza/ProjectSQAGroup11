TARGETS: BooleanUtils.toBoolean(String), toBooleanObject(String), toBoolean(String,String,String)
ORACLES: Expected: case-insensitive "true"/1-value→true, "false"/0-value→false,
null/empty/other→false (per Javadoc)
CASES: null, "", "true", "TRUE", "false", "FALSE", "tRuE","yes","no","1","0", len=1..3 strings
(e.g.,"tr","fa")
RISKS: Index OOB for strings with length <4 when substring logic assumes at least 3rd char exists;
same root may hit other Str-parsing methods
RISKS: toBooleanObject(String) likely shares same bug for short true/false inputs; test with custom
true/false strings shorter tan 3