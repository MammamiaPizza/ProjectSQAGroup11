TARGETS: ProcessCommonJSModules.toModuleName, ProcessCommonJSModules.normalizeSourceName
ORACLES: Expected module names inferred from testExpect (strip dir+ext, replace separators, prefix
"module$")
CASES: Normal: "foo/baz.js" → "module$baz"; "a/b/c.js" → "module$c"
CASES: Windows: "foo\bar.js" → "module$bar"; mixed "a/b\c.js"
CASES: Relative: "./foo/bar.js" → "module$bar"; ".\foo\bar.js" → "module$bar"
CASES: Edge: "", ".js", "foo/..", "..", null → should not crash
RISKS: Backslash normalization likely the bug; toModuleName is static, behavior may differ from full
pass