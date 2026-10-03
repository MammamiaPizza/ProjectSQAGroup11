TARGETS: PropertyBuilder.getDefaultValue() for JDK atomic types (AtomicReference, AtomicBoolean,
etc.)
ORACLES: Expected empty JSON from testEmpty1256; JsonInclude.Include.NON_NULL default semantics
CASES: AtomicReference(null) → omit; AtomicBoolean(null) → omit; non-null atomic → include existing
value
CASES: null String field already omitted; boundary: AtomicReference with custom non-null default;
error: BeanPropertyWriter null
RISKS: Only PropertyBuilder visible; internal inclusion check may involve BeanPropertyWriter; no
side-effects on other serializers