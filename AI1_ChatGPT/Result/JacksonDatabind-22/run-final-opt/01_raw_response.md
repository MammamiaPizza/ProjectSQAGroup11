TARGETS: BasicSerializerFactory serializer selection; BeanSerializerFactory.findBeanSerializer bean serializer construction  
ORACLES: Trigger asserts JSON numeric 42, not JSON string "value"  
CASES: @JsonValue type with a custom serializer override producing numeric output  
CASES: Verify custom override wins over default @JsonValue accessor serialization  
RISKS: Factory interaction may select cached/default scalar serializer instead of custom serializer  
RISKS: Context exposes only one failing custom-override scenario; no additional expected behaviors given