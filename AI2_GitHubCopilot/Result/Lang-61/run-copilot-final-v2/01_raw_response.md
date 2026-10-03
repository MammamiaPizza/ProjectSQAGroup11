TARGETS: StrBuilder.deleteAll(StrMatcher), indexOf(String,int), deleteImpl, replaceImpl — LANG-294
bug is in deleteAll/delete-loop logic
ORACLES: Expected behavior per bug report: deleteAll("") must leave buffer unchanged (not cause OOB
or wrong result); indexOf("", 0) should return 0
CASES: deleteAll with empty-string matcher; indexOf("", startIndex) at 0, positive, size, boundary;
lastIndexOf("", indices); replaceAll("", "...")
RISKS: Bug details limited to LANG-294 — no access to buggy code, only public API; exact deleteAll
logic unknown beyond indexOf-driven loop defect