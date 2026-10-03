@org.junit.Test
public void testInitialCapacityConstructorSupportsCaseInsensitiveLookup() {
    org.apache.commons.collections.map.CaseInsensitiveMap map =
            new org.apache.commons.collections.map.CaseInsensitiveMap(2);

    map.put("CapacityKey", "value");

    org.junit.Assert.assertEquals("value", map.get("capacitykey"));
}

@org.junit.Test
public void testInitialCapacityAndLoadFactorConstructorSupportsCaseInsensitiveLookup() {
    org.apache.commons.collections.map.CaseInsensitiveMap map =
            new org.apache.commons.collections.map.CaseInsensitiveMap(2, 0.75f);

    map.put("LoadFactorKey", "value");

    org.junit.Assert.assertEquals("value", map.get("LOADFACTORKEY"));
}

@org.junit.Test
public void testMapConstructorConvertsCopiedStringKeys() {
    java.util.Map source = new java.util.HashMap();
    source.put("CopiedKey", "value");

    org.apache.commons.collections.map.CaseInsensitiveMap map =
            new org.apache.commons.collections.map.CaseInsensitiveMap(source);

    org.junit.Assert.assertEquals("value", map.get("copiedkey"));
}

@org.junit.Test
public void testSerializationPreservesCaseInsensitiveEntries() throws Exception {
    org.apache.commons.collections.map.CaseInsensitiveMap original =
            new org.apache.commons.collections.map.CaseInsensitiveMap();
    original.put("SerializedKey", "value");
    original.put(null, "null-value");

    java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
    java.io.ObjectOutputStream output = new java.io.ObjectOutputStream(bytes);
    output.writeObject(original);
    output.close();

    java.io.ObjectInputStream input = new java.io.ObjectInputStream(
            new java.io.ByteArrayInputStream(bytes.toByteArray()));
    org.apache.commons.collections.map.CaseInsensitiveMap restored =
            (org.apache.commons.collections.map.CaseInsensitiveMap) input.readObject();
    input.close();

    org.junit.Assert.assertEquals("value", restored.get("serializedkey"));
    org.junit.Assert.assertEquals("null-value", restored.get(null));
}