TARGETS: ExternalTypeHandler.complete sets external type property on bean.
ORACLES: Bean property value after deserialization matches JSON type id string.
CASES: External type "foo" yields property "foo"; null type id yields null.
CASES: External type field before/after other properties; missing type field.
CASES: Type id references unknown subtype; property may remain null.
RISKS: Annotation config (@JsonTypeInfo use/visible) may alter expected behavior.
RISKS: Thread safety of ExternalTypeHandler if reused.