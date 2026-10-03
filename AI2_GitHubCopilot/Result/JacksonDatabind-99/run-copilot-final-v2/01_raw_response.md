TARGETS: ReferenceType.buildCanonicalName() canonical-name string for reference type wrapping arrays
ORACLES: TestTypeFactory.testCanonicalNames expected strings; expected format: "[...reference type,
class ...]"
ORACLES: Proper closing bracket/angle bracket nesting in canonical names matching Java type
representation
CASES: Wrapping array type (e.g., Long[]) — canonical name must preserve array brackets with closing
CASES: Wrapping simple class (e.g., String), wrapping parameterized type (e.g., List<String>)
CASES: Wrapping another ReferenceType (nested), wrapping null content type
CASES: Boundary: empty content, deeply nested references, multi-dimensional arrays
RISKS: Only buildCanonicalName() is modified; other type canonical names must remain unchanged
RISKS: Ensure upgradeFrom() and construct() produce instances whose canonical names are correct
RISKS: Cannot inspect actual buggy source; rely on trigger test failure message alone