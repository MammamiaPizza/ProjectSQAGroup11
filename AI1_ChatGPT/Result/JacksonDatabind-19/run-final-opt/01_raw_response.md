TARGETS: TypeFactory constructType/findTypeParameters resolution of Properties as Map<String,String>.
ORACLES: Trigger expectations: Properties values deserialize/convert as String ("123"/"129"), not null.
ORACLES: TestTypeFactory expects resolved Properties Map value raw class exactly String, not Object.
CASES: constructType(Properties.class); inspect Map key/value types for String.
CASES: ObjectMapper readValue into Properties with numeric-text entry; value must equal input text.
CASES: convertValue Map with numeric-text entry to Properties; retrieved value must equal input text.
RISKS: Context omits exact JavaType accessor APIs and Properties generic-resolution implementation.