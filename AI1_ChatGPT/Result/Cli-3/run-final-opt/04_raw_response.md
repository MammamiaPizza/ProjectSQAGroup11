public void testCreateObjectHandlesResolvableAndUnresolvableTypes()
{
    Object value = TypeHandler.createObject("java.lang.String");

    assertEquals(java.lang.String.class, value.getClass());
    assertEquals("", value);
    assertNull(TypeHandler.createObject("java.lang.Runnable"));
    assertNull(TypeHandler.createObject("no.such.Type"));
}

public void testCreateClassHandlesResolvableAndUnresolvableNames()
{
    assertEquals(java.lang.String.class, TypeHandler.createClass("java.lang.String"));
    assertNull(TypeHandler.createClass("no.such.Type"));
}

public void testCreateDateParsesFormattedDateAndRejectsInvalidInput()
{
    String date = java.text.DateFormat.getDateInstance().format(new java.util.Date(0L));

    assertNotNull(TypeHandler.createDate(date));
    assertNull(TypeHandler.createDate("not-a-date"));
}

public void testCreateValueCreatesUrlAndFileValues()
{
    Object url = TypeHandler.createValue("http://example.com/path", java.net.URL.class);

    assertNotNull(url);
    assertEquals("http://example.com/path", url.toString());
    assertNull(TypeHandler.createURL("not-a-url"));
    assertEquals(new java.io.File("target-file"),
                 TypeHandler.createValue("target-file", java.io.File.class));
}