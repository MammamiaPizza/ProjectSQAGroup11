TARGETS: Cleaner.clean(Document) with frameset-containing HTML; Cleaner.copySafeNodes handling of
<frameset>/<noframes>
ORACLES: assert no NullPointerException; assert Cleaner.isValid returns true after clean; compare
cleaned doc structure to expected from original handlesFramesets test
CASES: doc with <frameset><frame><noframes>; doc with only <frameset>; doc with <frameset> and
disallowed tags; empty doc; doc with body after frameset; Whitelist.basic()
RISKS: root cause of NPE unknown beyond trigger; cleaned output depends on Whitelist; no formal spec
for cleaned HTML; test must not rely on fragile internal details