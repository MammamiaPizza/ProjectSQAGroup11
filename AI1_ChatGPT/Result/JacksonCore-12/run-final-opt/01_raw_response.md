TARGETS: getTokenLocation(), getCurrentLocation(), _updateLocation(), _updateNameLocation() in both parsers
ORACLES: JsonLocation character/byte offsets from parser token traversal; trigger expects offset 6, not 1
CASES: Reader parser: object with successive fields; assert FIELD_NAME/value token locations after advancing tokens
CASES: UTF-8 stream parser: equivalent object-field traversal; compare token-location offset behavior
CASES: Boundary: first field, later field, and closing object locations to expose location state updates
RISKS: Exact input and asserted JsonLocation accessor from trigger are unavailable; avoid inferring unprovided offsets