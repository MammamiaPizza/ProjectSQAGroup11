TARGETS: MultiKey serialization round-trip integrity, subclass readResolve, equals/hashCode/size
after deserialization
ORACLES: original object state (keys, size, equals) must be preserved after deserialization, per
MultiKey contract
CASES: serialize/deserialize MultiKey with 2..5 keys; subclass of MultiKey; empty array constructor;
boundary size=1
RISKS: Derived class serialization details unknown; only MultiKey code available; test must not
depend on subclass internals