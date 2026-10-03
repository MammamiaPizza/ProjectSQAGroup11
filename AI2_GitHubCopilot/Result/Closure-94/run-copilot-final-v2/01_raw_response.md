TARGETS: NodeUtil.isValidDefineValue
ORACLES: @define vars should accept any literal initializer; failing tests prove strings must be
valid
CASES: string literals (empty, typical, special chars, escaped quotes), numeric, boolean, null
CASES: invalid: function, var reference, operator, call, Object/Array literals, undefined node
CASES: boundary: val=null, defines=null/empty, non-trivial string with unicode/surrogates
RISKS: only signatures provided; no implementation or Javadoc for isValidDefineValue
RISKS: defines set may influence validity; interaction with ProcessDefines not fully visible