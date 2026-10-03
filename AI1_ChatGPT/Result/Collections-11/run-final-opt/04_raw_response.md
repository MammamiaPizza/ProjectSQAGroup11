@org.junit.Test
public void equalsReturnsTrueForSameInstance() {
    org.apache.commons.collections.keyvalue.MultiKey key =
            new org.apache.commons.collections.keyvalue.MultiKey("a", "b");

    org.junit.Assert.assertTrue(key.equals(key));
}

@org.junit.Test
public void toStringListsKeysInOrder() {
    org.apache.commons.collections.keyvalue.MultiKey key =
            new org.apache.commons.collections.keyvalue.MultiKey("a", null, "c");

    org.junit.Assert.assertEquals("MultiKey[a, null, c]", key.toString());
}