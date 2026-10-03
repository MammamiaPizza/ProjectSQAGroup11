TARGETS: ArgumentImpl.validate() error message when unexpected token found; must not quote token.
TARGETS: WriteableCommandLineImpl.looksLikeOption() option matching to avoid look-alike loops.
ORACLES: Expected message "Unexpected <token> while processing <input>" from
BugLoopingOptionLookAlikeTest assertion.
CASES: Normal: valid arg parsed; no error. Boundary: value with initial/subsequent separators.
Error: token that looksLikeOption.
CASES: Error: consumeRemaining "--" handling; missing required argument; value count exceeds
maximum.
RISKS: Only one message variant tested; defaults, property, and switch paths may have similar
quoting bugs.
RISKS: Cannot verify stripBoundaryQuotes behavior or how initial/subsequent separators affect token
extraction.