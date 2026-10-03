TARGETS: StringUtils.containsIgnoreCase(String,String), especially locale-independent Unicode case matching.
ORACLES: Trigger expects true for "ß" containing "SS" at index 0 across default locales.
CASES: Set default locales (e.g., English/Turkish); assert containsIgnoreCase("ß","SS") is true.
CASES: Reverse direction, embedded matches, ASCII equal/different case, and absent substring.
CASES: Null haystack/needle and empty needle behavior, if established by existing tests/API context.
RISKS: Case folding can expand characters; Java region matching may not equate ß with SS.
RISKS: Context provides only one trigger; avoid assuming behavior for other Unicode expansions.