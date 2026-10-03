@Test
public void customFormatsWorkWhenSurroundedByQuotedBraces() {
    java.util.Map<String, org.apache.commons.lang3.text.FormatFactory> registry =
            new java.util.HashMap<String, org.apache.commons.lang3.text.FormatFactory>();
    registry.put("padded", new org.apache.commons.lang3.text.FormatFactory() {
        public java.text.Format getFormat(String name, String arguments, java.util.Locale locale) {
            return new java.text.Format() {
                private static final long serialVersionUID = 1L;

                public StringBuffer format(Object object, StringBuffer toAppendTo,
                        java.text.FieldPosition pos) {
                    return toAppendTo.append("00").append(object);
                }

                public Object parseObject(String source, java.text.ParsePosition pos) {
                    return null;
                }
            };
        }
    });

    ExtendedMessageFormat format =
            new ExtendedMessageFormat("'{'{0,padded}'}'", registry);

    assertEquals("{007}", format.format(new Object[] { Integer.valueOf(7) }));
}

@Test
public void escapedQuotesAreHandledLikeMessageFormatQuotes() {
    ExtendedMessageFormat format = new ExtendedMessageFormat("It''s {0}");

    assertEquals("It's working", format.format(new Object[] { "working" }));
}

@Test(expected = IllegalArgumentException.class)
public void nonNumericArgumentIndexesAreRejected() {
    new ExtendedMessageFormat("{argument}");
}