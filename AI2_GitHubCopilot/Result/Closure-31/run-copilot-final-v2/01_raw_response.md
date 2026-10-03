TARGETS: Compiler.compile(), run(), dependency sorting during whitespace mode; module graph
traversal order.
ORACLES: expected sorted output via toSource(); no errors; correct module ordering after compile.
CASES: single module, multi-module chain, empty module, whitespace-only vs full compilation,
dependency reversal test.
RISKS: exact bug unknown; rely on public API; no access to internal passes; whitespace mode state
may be hidden.