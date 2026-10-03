TARGETS: TokenQueue.consumeAttributeKey, chompBalanced, consumeToAny
ORACLES: CSS selector spec: quoted attribute values may contain brackets
CASES: Quoted attr values with single/double quotes containing ']', "]", '[][]'
CASES: Unbalanced brackets inside quotes, empty value, adjacent brackets
CASES: Escaped brackets in quoted values, escaped quotes within quotes
RISKS: Only buggy version context; cannot validate against fix or reference parser