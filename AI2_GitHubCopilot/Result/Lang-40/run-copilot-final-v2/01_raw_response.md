TARGETS: org.apache.commons.lang.StringUtils.containsIgnoreCase(String,String)
ORACLES: Locale-independent; use toUpperCase(Locale.ENGLISH); "ß" equalsIgnoreCase "SS" => true
ORACLES: All ignore-case methods must be consistent across Locales (e.g., EN, TR, DE)
CASES: normal ASCII ("abc","ABC")->true; null first->false; null second->false; empty str->false
CASES: "Straße" containsIgnoreCase "STRASSE" => true; "SS" containsIgnoreCase "ß" => true
CASES: "ß","ß" => true; "abc","def" => false; identical strings different case; non-letter chars
CASES: Turkish locale: "i" vs "I" — locale-independent means English-style mapping, "i" contains "I"
=> true
CASES: Boundary: very long strings (10k chars), repeated substrings, whitespace-only, Unicode
supplement
RISKS: equalsIgnoreCase, startsWithIgnoreCase, endsWithIgnoreCase may have same locale-sensitive bug
RISKS: Test environment may not install all locales; must verify with at least EN and TR if possible