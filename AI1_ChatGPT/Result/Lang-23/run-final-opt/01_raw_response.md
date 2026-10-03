TARGETS: ExtendedMessageFormat.equals(Object) and hashCode() registry-sensitive equality contract.
ORACLES: Existing trigger asserts differing registries yield differing hash codes.
CASES: Same pattern/locale/registry: equal objects must have equal hash codes.
CASES: Same pattern/locale with different registry instances/content: equality/hash-code distinction.
CASES: Null and empty registries; registry map with at least one FormatFactory.
RISKS: No source or existing test details supplied beyond failing registry hashcode assertion.