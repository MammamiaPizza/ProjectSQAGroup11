@org.junit.Test
public void testStringConstructorsInitializeExpectedContent() {
    org.apache.commons.lang.text.StrBuilder empty =
            new org.apache.commons.lang.text.StrBuilder((String) null);
    org.junit.Assert.assertEquals(0, empty.length());
    org.junit.Assert.assertEquals("", empty.toString());

    org.apache.commons.lang.text.StrBuilder builder =
            new org.apache.commons.lang.text.StrBuilder("initial");
    org.junit.Assert.assertEquals(7, builder.length());
    org.junit.Assert.assertEquals("initial", builder.toString());

    org.apache.commons.lang.text.StrBuilder zeroCapacity =
            new org.apache.commons.lang.text.StrBuilder(0);
    zeroCapacity.append("value");
    org.junit.Assert.assertEquals("value", zeroCapacity.toString());
}

@org.junit.Test
public void testAppendObjectUsesObjectStringValue() {
    org.apache.commons.lang.text.StrBuilder builder =
            new org.apache.commons.lang.text.StrBuilder();
    Object value = new Object() {
        public String toString() {
            return "object-value";
        }
    };

    builder.append(value);

    org.junit.Assert.assertEquals("object-value", builder.toString());
}

@org.junit.Test
public void testAppendStringRangeAppendsRequestedCharactersAndRejectsInvalidRanges() {
    org.apache.commons.lang.text.StrBuilder builder =
            new org.apache.commons.lang.text.StrBuilder();

    builder.append("abcdef", 2, 3);
    builder.append("abcdef", 6, 0);
    org.junit.Assert.assertEquals("cde", builder.toString());

    try {
        builder.append("abcdef", -1, 1);
        org.junit.Assert.fail("Expected StringIndexOutOfBoundsException");
    } catch (java.lang.StringIndexOutOfBoundsException expected) {
        org.junit.Assert.assertEquals("cde", builder.toString());
    }

    try {
        builder.append("abcdef", 4, 3);
        org.junit.Assert.fail("Expected StringIndexOutOfBoundsException");
    } catch (java.lang.StringIndexOutOfBoundsException expected) {
        org.junit.Assert.assertEquals("cde", builder.toString());
    }
}

@org.junit.Test
public void testAppendNumericValuesUsesStandardStringRepresentations() {
    org.apache.commons.lang.text.StrBuilder builder =
            new org.apache.commons.lang.text.StrBuilder();

    builder.append(12).append(",").append(34L).append(",")
            .append(1.5f).append(",").append(2.0d);

    org.junit.Assert.assertEquals("12,34,1.5,2.0", builder.toString());
}