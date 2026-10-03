TARGETS: extractClassNameIfRequire(Node,Node) — returns non-null for non‑goog.require calls
ORACLES: DefaultCodingConvention semantics; junit assertions from testRequire (null expected)
CASES: goog.require('a.B')→'a.B'; goog.provide('a.B')→null; bare require('foo')→null
CASES: null nodes, non‑NAME first child, non‑CALL parent, wrong argument count/type
CASES: goog.require without string arg; x.y.require('foo')→null; similar-prefix calls
RISKS: Exact failing input unknown; bug likely in lax call‑name or arg‑count validation