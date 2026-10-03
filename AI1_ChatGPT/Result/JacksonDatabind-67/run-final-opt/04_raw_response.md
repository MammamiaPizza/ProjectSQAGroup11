@org.junit.Test
public void customKeyDeserializerModifierIsAppliedToRegularEnumMapKeys() throws Exception {
    com.fasterxml.jackson.databind.module.SimpleModule module =
            new com.fasterxml.jackson.databind.module.SimpleModule();
    module.setDeserializerModifier(new com.fasterxml.jackson.databind.deser.BeanDeserializerModifier() {
        @Override
        public com.fasterxml.jackson.databind.KeyDeserializer modifyKeyDeserializer(
                com.fasterxml.jackson.databind.DeserializationConfig config,
                com.fasterxml.jackson.databind.JavaType type,
                com.fasterxml.jackson.databind.KeyDeserializer deserializer) {
            if (type.getRawClass() == KeyEnum.class) {
                return new com.fasterxml.jackson.databind.KeyDeserializer() {
                    @Override
                    public Object deserializeKey(String key,
                            com.fasterxml.jackson.databind.DeserializationContext ctxt)
                            throws java.io.IOException {
                        return KeyEnum.valueOf(key.toLowerCase(java.util.Locale.ROOT));
                    }
                };
            }
            return deserializer;
        }
    });

    java.util.Map<KeyEnum, Integer> result = new com.fasterxml.jackson.databind.ObjectMapper()
            .registerModule(module)
            .readValue("{\"REPlaceMENTS\":9}",
                    new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<KeyEnum, Integer>>() { });

    org.junit.Assert.assertEquals(Integer.valueOf(9), result.get(KeyEnum.replacements));
}