TARGETS: $Gson$Types.getCollectionElementType, getMapKeyAndValueTypes, resolve, getSupertype,
ParameterizedTypeImpl
ORACLES: Expected type from known generic supertypes (e.g., Collection<String>, Map<String,Integer>)
using getRawType
CASES: Nested generics (List<Map<String, SmallClass>>), wildcards, raw types, multi-level
hierarchies
RISKS: Only modified class provided; no reference implementation; LinkedTreeMap behavior may differ
from expected Map subtype