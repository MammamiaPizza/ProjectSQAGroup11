TARGETS: IOUtils.skip(InputStream,long) – return value correctness when InputStream.skip returns
less.
ORACLES: Expected skip count from test assertions (10); verify against total bytes skipped using
read fallback.
CASES: InputStream.skip returns 0 (pure read fallback), returns partial (mix skip+read), returns
full desired; early EOF; negative numToSkip.
RISKS: IOUtils.internal implementation unknown; skip fallback logic may differ from standard
skip-then-read pattern; limited to public API signatures.