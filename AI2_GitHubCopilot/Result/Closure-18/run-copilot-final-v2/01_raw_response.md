TARGETS: Compiler.repartitionInputs() reorders inputs based on goog.provide/require.
TARGETS: Compiler.compileInternal() calls repartitionInputs before further passes.
ORACLES: Sorted input order must satisfy the goog.provide/require dependency graph.
ORACLES: Correct order is the topological sort of namespace dependencies among inputs.
CASES: Normal acyclic chain of provides/requires yields expected sorted input sequence.
CASES: Boundary: file provides multiple namespaces fulfilled by other inputs, order updated.
CASES: Boundary: single input with no dependencies – order unchanged.
CASES: Error: circular dependency raises a Compiler diagnostic (e.g., cycle error).
RISKS: Dependency resolution logic is internal; verify through input order after compile.
RISKS: Private helpers not directly testable; rely on observable sorted order or error count.