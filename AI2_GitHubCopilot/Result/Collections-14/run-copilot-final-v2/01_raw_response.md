TARGETS: CaseInsensitiveMap.convertKey(Object) – must use locale-independent case folding
ORACLES: get(anyCaseKey) should return put value; containsKey must reflect case-insensitive presence
CASES: Turkish locale: put("Key","v"), get("KEY") returns null (bug)
CASES: English locale: put("Key","v"), get("key") returns "v" (baseline)
CASES: Overwrite: put("key","x"), put("KEY","y") -> get("key") returns "y"
CASES: Empty string key put/get; null key get (expect exception)
CASES: remove("key") with Turkish locale after put("KEY","v") removes entry
RISKS: Save/restore Locale.setDefault in setUp/tearDown; check Turkish locale available
RISKS: Other locales may also fail (Greek, Azeri) but focus on known trigger
CASES: put("i","a") with Turkish locale then get("I") returns null (dotless I case)