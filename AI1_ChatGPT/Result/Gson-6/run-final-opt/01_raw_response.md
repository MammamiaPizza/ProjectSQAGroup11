TARGETS: JsonAdapterAnnotationTypeAdapterFactory.create(Gson, TypeToken) for @JsonAdapter-annotated raw types  
ORACLES: Trigger tests require deserialize and serialize paths to avoid NullPointerException  
CASES: @JsonAdapter TypeAdapter handling null input during deserialization  
CASES: @JsonAdapter TypeAdapter handling null value during serialization  
CASES: Annotation value assignable to TypeAdapter and to TypeAdapterFactory  
RISKS: Context omits @JsonAdapter nullSafe attribute semantics and adapter test fixtures  
RISKS: Do not infer output JSON or returned values beyond trigger-observed NPE absence