TARGETS: TypeInfoFactory.getTypeInfoForField(Field, Type typeDefiningF) resolving a field of type
variable T into a parameterized TypeInfo.
TARGETS: getIndex(TypeVariable<?>[], TypeVariable<?>) and fallback path when the type variable is
missing.
ORACLES: TypeVariableTest::testSingle passes; no UnsupportedOperationException "got class ...$Bar";
TypeInfo matches resolved parameterized type.
CASES: Normal — field T with concrete typeDefiningF (e.g., Bar<Integer>); direct vs inherited type
variable.
CASES: Boundary — raw Bar.class as typeDefiningF; array T[] via getTypeInfoForArray;
empty/mismatched type-variable arrays.
CASES: Error — type variable absent from class declaration; unresolved generic typeDefiningF should
not throw unsupported exception.
RISKS: Only modified class and trigger are supplied; expected resolved concrete type is not fully
specified here.
RISKS: Trigger asserts only testSingle; avoid inventing additional Gson APIs or expected field
values.