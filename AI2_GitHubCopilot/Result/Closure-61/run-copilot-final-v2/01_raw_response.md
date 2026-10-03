TARGETS: NodeUtil.mayHaveSideEffects(Node), mayEffectMutableState(Node), callHasLocalResult(Node),
 TARGETS: evaluatesToLocalValue(Node), nodeTypeMayHaveSideEffects(Node), isValidDefineValue(Node)
ORACLES: Test assertions that unused void/comma/typeof/pure-call ops are removed; spec that
literals,
 ORACLES: Math.sin, String, and similar have no side effects; expected dead-code elimination by
PeepholeRemoveDeadCode
CASES: Void 0, comma expressions, typeof x, pure calls (Math.sin, String), assignment ops with
unused result,
 CASES: boundary: MAX_POSITIVE_INTEGER_NUMBER literals, NaN, Infinity; new/constructor calls that
may mutate state
RISKS: Limited to API signatures; actual bug may involve internal helpers not visible; state of
AbstractCompiler may matter;
 RISKS: missing context on how NodeUtil results are consumed by PeepholeRemoveDeadCode; assumptions
based on test names only