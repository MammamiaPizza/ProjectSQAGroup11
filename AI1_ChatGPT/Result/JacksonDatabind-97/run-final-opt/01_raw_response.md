TARGETS: POJONode.serialize(JsonGenerator, SerializerProvider) for embedded custom POJO values.
TARGETS: POJONode constructor/getPojo/asText for wrapped-value preservation.
ORACLES: Existing trigger expects custom serialization field value "The value is: Hello!", not "NULL".
CASES: Wrap a POJO with a custom serializer; serialize through ObjectMapper and compare generated JSON.
CASES: Verify custom serializer receives/uses the non-null wrapped POJO value.
CASES: Normal asText returns wrapped object's toString(); null wrapper returns "null".
RISKS: Context provides only the failing custom-serializer scenario; no broader serialization contract is shown.