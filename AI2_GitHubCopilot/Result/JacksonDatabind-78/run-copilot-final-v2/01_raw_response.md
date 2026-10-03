TARGETS: BeanDeserializerFactory method that validates bean type legality (e.g., near
isPotentialBeanType) and throws with "[Illegal type]"
ORACLES: Exception message must contain substring "[Illegal type]" (from
IllegalTypesCheckTest::testIssue1599 assertion)
CASES: Provide a known illegal type (e.g., java.lang.Class) -> expect IllegalArgumentException with
"Illegal type"
CASES: Provide a Proxy class -> expects exception message "Illegal type", NOT "N/A"
CASES: Normal bean type (e.g., java.util.UUID) -> no exception, deserialization proceeds
CASES: Boundary: non-static inner class or abstract class -> may be illegal, must produce "Illegal
type" if blocked
RISKS: Exact list of illegal types may be configurable/extensible; context does not reveal the full
blacklist
RISKS: "N/A" message suggests a bypass code path; ensure all illegal-type checks are reached before
any other exception
RISKS: The method throwing "N/A" may be unrelated property validation; verify bean-type rejection is
triggered earlier