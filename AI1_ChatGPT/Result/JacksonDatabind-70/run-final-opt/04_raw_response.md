@org.junit.Test
public void removalHandlesPropertiesStoredInPrimarySecondaryAndSpillSlots() throws Exception {
    com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap map =
            com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap.construct(
                    propertiesFor(CollisionBean.class), false);

    String[] collidingNames = new String[] { "a", "q", "A" };
    for (String name : collidingNames) {
        org.junit.Assert.assertNotNull(map.find(name));
    }

    for (String name : collidingNames) {
        com.fasterxml.jackson.databind.deser.SettableBeanProperty property = map.find(name);
        org.junit.Assert.assertNotNull(property);
        map.remove(property);
        org.junit.Assert.assertNull(map.find(name));
    }

    org.junit.Assert.assertEquals(3, map.size());
    org.junit.Assert.assertNotNull(map.find("b"));
    org.junit.Assert.assertNotNull(map.find("c"));
    org.junit.Assert.assertNotNull(map.find("d"));
}

@org.junit.Test
public void renameAllUsesTransformedNamesWithoutChangingOriginalMap() throws Exception {
    com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap original =
            com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap.construct(
                    propertiesFor(RenameBean.class), false);
    com.fasterxml.jackson.databind.util.NameTransformer transformer =
            new com.fasterxml.jackson.databind.util.NameTransformer() {
                @Override
                public String transform(String name) {
                    return "renamed_" + name;
                }

                @Override
                public String reverse(String transformed) {
                    if (transformed.startsWith("renamed_")) {
                        return transformed.substring("renamed_".length());
                    }
                    return null;
                }
            };

    com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap renamed =
            original.renameAll(transformer);

    org.junit.Assert.assertEquals(2, renamed.size());
    org.junit.Assert.assertNotNull(renamed.find("renamed_first"));
    org.junit.Assert.assertNotNull(renamed.find("renamed_last"));
    org.junit.Assert.assertNull(renamed.find("first"));
    org.junit.Assert.assertNotNull(original.find("first"));
    org.junit.Assert.assertNotNull(original.find("last"));
}

private static java.util.Collection<com.fasterxml.jackson.databind.deser.SettableBeanProperty> propertiesFor(
        Class<?> type) throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper =
            new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.DeserializationContext context =
            mapper.getDeserializationContext().createInstance(
                    mapper.getDeserializationConfig(), null, null);
    com.fasterxml.jackson.databind.JsonDeserializer<?> deserializer =
            context.findRootValueDeserializer(mapper.getTypeFactory().constructType(type));

    org.junit.Assert.assertTrue(
            deserializer instanceof com.fasterxml.jackson.databind.deser.BeanDeserializerBase);

    java.util.ArrayList<com.fasterxml.jackson.databind.deser.SettableBeanProperty> properties =
            new java.util.ArrayList<com.fasterxml.jackson.databind.deser.SettableBeanProperty>();
    java.util.Iterator<com.fasterxml.jackson.databind.deser.SettableBeanProperty> iterator =
            ((com.fasterxml.jackson.databind.deser.BeanDeserializerBase) deserializer).properties();
    while (iterator.hasNext()) {
        properties.add(iterator.next());
    }
    return properties;
}

public static class CollisionBean {
    public int a;
    public int q;
    public int A;
    public int b;
    public int c;
    public int d;
}

public static class RenameBean {
    public int first;
    public int last;
}