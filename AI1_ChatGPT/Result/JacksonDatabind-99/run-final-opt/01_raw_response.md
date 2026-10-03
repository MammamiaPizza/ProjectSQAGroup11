TARGETS: ReferenceType.buildCanonicalName(), via TypeFactory canonical-name construction.  
ORACLES: Trigger expects nested reference canonical name ending `java.lang.Long>` (not `Long[]`).  
CASES: Construct/reference a parameterized reference type with Long content; assert canonical name.  
CASES: Nested/reference content canonical naming, including closing generic delimiter(s).  
ORACLES: Existing TestTypeFactory.testCanonicalNames comparison failure and JavaType canonical-name output.  
RISKS: buildCanonicalName is protected; test through public TypeFactory/JavaType APIs used by trigger.  
RISKS: Available context omits exact TypeFactory construction signatures and broader expected canonical formats.