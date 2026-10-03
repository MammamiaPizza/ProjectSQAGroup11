TARGETS: Jsoup.isValid(String,Whitelist),Cleaner.isValid(Document),Cleaner.clean(Document)
ORACLES: clean output equals input → isValid true; else false. Whitelist controls allowed
tags/attrs.
CASES: valid body fragment (no disallowed tags), invalid (has <b> or <script> per whitelist),
empty/whitespace-only input, document with <head> + <body>, only text nodes.
RISKS: Only testIsValidBodyHtml and testIsValidDocument fail; related
Jsoup.isValid(String,Whitelist) may also be affected. The bug likely in Cleaner.isValid returning
wrong boolean.