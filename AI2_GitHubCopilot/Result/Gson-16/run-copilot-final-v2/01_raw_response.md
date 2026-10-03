TARGETS: $Gson$Types.resolveTypeVariable for recursive type-variable bounds
TARGETS: resolve when toResolve is a TypeVariable with self/cyclic bounds
ORACLES: No StackOverflowError; resolution terminates
ORACLES: Resolved type equals Java reflection-based resolution via getGenericSuperclass
CASES: Self-recursive bound T extends T (direct identity)
CASES: Parameterized self-recursive T extends Comparable<T>; mutual recursion across 2+ vars
CASES: Deep finite recursive chain (non-cyclic), e.g., A<B<A<...>>> nesting depth 10
CASES: TypeVariable bound that is itself a ParameterizedType referencing the same var
RISKS: Missing test code and expected resolved types; must infer termination behavior only
RISKS: True infinite recursion (ill-formed bound) should throw, not StackOverflow — distinction
unclear