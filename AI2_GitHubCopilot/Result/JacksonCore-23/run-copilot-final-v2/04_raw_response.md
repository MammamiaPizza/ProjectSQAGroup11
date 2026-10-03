@org.junit.Test
    public void testWithSpacesInObjectEntriesSameStateReturnsThis() {
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
        org.junit.Assert.assertTrue(pp == pp.withSpacesInObjectEntries());
    }

 @org.junit.Test
 public void testWithoutSpacesInObjectEntriesCreatesNewInstanceAndBack() {
     DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
     DefaultPrettyPrinter noSpaces = pp.withoutSpacesInObjectEntries();
     org.junit.Assert.assertTrue(noSpaces != pp);
     org.junit.Assert.assertTrue(noSpaces == noSpaces.withoutSpacesInObjectEntries());
     DefaultPrettyPrinter withSpaces = noSpaces.withSpacesInObjectEntries();
     org.junit.Assert.assertTrue(withSpaces != noSpaces);
     org.junit.Assert.assertTrue(withSpaces == withSpaces.withSpacesInObjectEntries());
 }

 @org.junit.Test
 public void testIndenterIdentityAndNullCoercion() {
     DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
     org.junit.Assert.assertTrue(pp ==
pp.withArrayIndenter(DefaultPrettyPrinter.FixedSpaceIndenter.instance));
     DefaultPrettyPrinter arrayNull = pp.withArrayIndenter(null);
     org.junit.Assert.assertTrue(arrayNull != pp);
     org.junit.Assert.assertTrue(arrayNull == arrayNull.withArrayIndenter(null));
     org.junit.Assert.assertTrue(pp ==
pp.withObjectIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE));
     DefaultPrettyPrinter objectNull = pp.withObjectIndenter(null);
     org.junit.Assert.assertTrue(objectNull != pp);
     org.junit.Assert.assertTrue(objectNull == objectNull.withObjectIndenter(null));
 }

 @org.junit.Test
 public void testWithRootSeparatorSameInstanceReturnsThis() {
     DefaultPrettyPrinter same = new DefaultPrettyPrinter("~");
     org.junit.Assert.assertTrue(same == same.withRootSeparator("~"));
     DefaultPrettyPrinter nullSep = new
DefaultPrettyPrinter((com.fasterxml.jackson.core.io.SerializableString) null);
     org.junit.Assert.assertTrue(nullSep ==
nullSep.withRootSeparator((com.fasterxml.jackson.core.io.SerializableString) null));
 }