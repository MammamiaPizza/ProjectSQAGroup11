TARGETS: NullPropertyPointer.createPath(JXPathContext), createPath(JXPathContext,Object), setValue,
getBaseValue, setPropertyName
ORACLES: createPath must not throw AssertionFailedError when factory is broken; expected behaviour
from JXPath contract (path creation vs. error)
CASES: empty/null property name via setPropertyName
CASES: factory returns null for intermediate bean; createPath with non-null value
CASES: setPropertyIndex then createPath on null pointer; parent pointer is null/valid
CASES: setValue(null) then getBaseValue; isActual after createPath on null property
CASES: createPath with Object value null; sequence: setValue then createPath then getImmediateNode
RISKS: Exact "badly implemented factory" unknown; assumptions from test name; no doc/spec; parent
BeanPointer interaction unverified