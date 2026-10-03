@org.junit.Test
public void escapeUsesNamedEntitiesNumericEntitiesAndLiteralCharacters() {
    org.junit.Assert.assertEquals("&amp;&lt;&#128;A",
            Entities.HTML40.escape("&<\u0080A"));
}

@org.junit.Test
public void writerEscapeUsesNamedEntitiesNumericEntitiesAndLiteralCharacters()
        throws java.io.IOException {
    java.io.StringWriter writer = new java.io.StringWriter();

    Entities.HTML40.escape(writer, "\u0080&x");

    org.junit.Assert.assertEquals("&#128;&amp;x", writer.toString());
}