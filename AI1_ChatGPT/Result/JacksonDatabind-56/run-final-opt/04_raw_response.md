@Test
public void deserializesHyphenSeparatedLanguageAndCountry() throws Exception {
    java.util.Locale locale = new com.fasterxml.jackson.databind.ObjectMapper()
            .readValue("\"en-US\"", java.util.Locale.class);

    assertEquals(new java.util.Locale("en", "US"), locale);
    assertEquals("en_US", locale.toString());
}