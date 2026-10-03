TARGETS: reportCodeChange() must not be called when compilation yields no code changes.
ORACLES: Override reportCodeChange in test subclass to record calls; assert no invocations.
CASES: Compile empty modules; optimize with no nodes; processDefines without defines; normal
unchanged run.
RISKS: Internal passes may trigger reportCodeChange via package-private code; no direct access to
debugLog/tracker internals.