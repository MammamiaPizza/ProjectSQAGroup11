@Test
    public void testConstructorWithPatternOnly() {
        ExtendedMessageFormat fmt = new ExtendedMessageFormat("{0}");
        assertEquals("{0}", fmt.toPattern());
    }

 @Test
 public void testConstructorWithPatternAndRegistry() {
     java.util.Map<String, FormatFactory> reg = new java.util.HashMap<String, FormatFactory>();
     ExtendedMessageFormat fmt = new ExtendedMessageFormat("{0}", reg);
     assertEquals("{0}", fmt.toPattern());
 }

 @Test
 public void testApplyPatternWithQuotedString() {
     ExtendedMessageFormat fmt = new ExtendedMessageFormat("");
     fmt.applyPattern("'{0}'");
     assertEquals("'{0}'", fmt.toPattern());
 }

 @Test
 public void testEqualsWithRegistryContainingOnlyNullValues() {
     java.util.Map<String, FormatFactory> reg1 = new java.util.HashMap<String, FormatFactory>();
     reg1.put("key", null);
     java.util.Map<String, FormatFactory> reg2 = new java.util.HashMap<String, FormatFactory>();
     reg2.put("key", null);
     ExtendedMessageFormat fmt1 = new ExtendedMessageFormat("pattern", java.util.Locale.US, reg1);
     ExtendedMessageFormat fmt2 = new ExtendedMessageFormat("pattern", java.util.Locale.US, reg2);
     assertTrue("should be equal when both registries have only null entries", fmt1.equals(fmt2));
     assertEquals("hashcodes should match", fmt1.hashCode(), fmt2.hashCode());
 }