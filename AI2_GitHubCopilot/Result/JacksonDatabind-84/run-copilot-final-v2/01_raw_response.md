TARGETS setReference(JavaType), getSelfReferencedType(), getGenericSignature(StringBuilder),
toString()
ORACLES after setReference, getSelfReferencedType returns the passed type; getGenericSignature
appends referenced type’s signature
ORACLES serialization must include supertype properties once the recursive reference is resolved
properly
CASES setReference then getSelfReferencedType identity and generic signature contains base class
info
CASES null _referencedType (default); calls before setReference; multiple setReference calls update
correctly
CASES ordering: create ResolvedRecursiveType, later setReference to resolved type, verify signature
includes base
CASES recursive container types (e.g., List of self) set reference to full resolved type; signature
reflects nesting
RISKS cannot verify full JSON output without ObjectMapper; must rely on type-resolution correctness
as proxy for serialization
RISKS ordering bug may depend on TypeFactory caching not exercised by isolated unit tests on the
class alone