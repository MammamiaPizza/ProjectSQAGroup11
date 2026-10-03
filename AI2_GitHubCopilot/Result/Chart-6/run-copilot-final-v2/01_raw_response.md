TARGETS: ShapeList.equals,hashCode,clone,serialization round-trip.
ORACLES: equals must be reflexive,symmetric,transitive; hashCode consistent; clone distinct but
equal.
ORACLES: Serialized copy equals original; hashCode unchanged after round-trip.
CASES: Empty list equals empty; same shapes equal; diff shapes not; null/type compare false.
CASES: Clone distinct but equals; clone hashCode same; serialization of empty/one/many shapes.
CASES: Boundary: setShape/getShape invalid index (expect exception if contract). null shape entry?
RISKS: Private writeObject/readObject untestable directly; test via Object stream round-trip.
RISKS: Bug unknown; tests must reveal equals or serialization failure; context buggy-only.
RISKS: AbstractObjectList may impact equals/serialization; focus on ShapeList-specific state.