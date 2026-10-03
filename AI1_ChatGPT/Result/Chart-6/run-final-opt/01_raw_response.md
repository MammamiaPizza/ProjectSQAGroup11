TARGETS: ShapeList.equals(Object), hashCode(), writeObject/readObject(), getShape(int), setShape(int,Shape)
ORACLES: Existing ShapeListTests testEquals and testSerialization assertion outcomes
CASES: Equal ShapeLists containing equal Shape entries; compare against non-ShapeList and null
CASES: Serialize/deserialize ShapeList with Shape entries; restored list must equal original
CASES: Empty list and null Shape entries through equality and serialization
RISKS: Shape implementations may not use Object.equals; serialization requires shape-aware handling
RISKS: No source diff or stated specification beyond failing trigger tests; avoid unsupported expectations