TARGETS: visit(NodeTraversal,Node,Node) for warning side-effect-free nodes; protectSideEffects() for
exemptions; process/hotSwapScript for integration
ORACLES: warning count from CheckSideEffects process; existing testUselessCode assertions for
expected warnings and repeats
CASES: normal useless expr "1;", "{};", "null;"; boundary empty script; error malformed JS; the
specific missed-warning input from Bug 753
RISKS: CheckSideEffectsTest harness not available; Bug 753 details unknown; must infer test setup
from trigger testUselessCode