@org.junit.Test
public void namedEntityLookupRecognizesKnownAndUnknownNames() {
    org.junit.Assert.assertTrue(Entities.isNamedEntity("amp"));
    org.junit.Assert.assertTrue(Entities.isBaseNamedEntity("amp"));
    org.junit.Assert.assertEquals(Character.valueOf('&'), Entities.getCharacterByName("amp"));
    org.junit.Assert.assertFalse(Entities.isNamedEntity("notAnEntity"));
    org.junit.Assert.assertFalse(Entities.isBaseNamedEntity("notAnEntity"));
    org.junit.Assert.assertNull(Entities.getCharacterByName("notAnEntity"));
}

@org.junit.Test
public void escapeModeMapsContainTheAmpersandEntity() {
    org.junit.Assert.assertEquals("amp", Entities.EscapeMode.xhtml.getMap().get('&'));
    org.junit.Assert.assertEquals("amp", Entities.EscapeMode.base.getMap().get('&'));
    org.junit.Assert.assertEquals("amp", Entities.EscapeMode.extended.getMap().get('&'));
}

@org.junit.Test
public void unescapeDecodesStandardNamedEntitiesInBothModes() {
    org.junit.Assert.assertEquals("<>&", Entities.unescape("&lt;&gt;&amp;"));
    org.junit.Assert.assertEquals("<>&", Entities.unescape("&lt;&gt;&amp;", true));
}