@org.junit.Test
public void abbreviateClampsUpperAfterClampingLowerPastEndOfString() {
    org.junit.Assert.assertEquals("0123456789abcde",
            WordUtils.abbreviate("0123456789abcde", 20, 10, "..."));
}

@org.junit.Test
public void capitalizeHandlesDefaultCustomAndEmptyDelimiters() {
    org.junit.Assert.assertEquals("I Am Fine", WordUtils.capitalize("i am fine"));
    org.junit.Assert.assertEquals("A.B c", WordUtils.capitalize("a.b c", new char[] { '.' }));
    org.junit.Assert.assertEquals("a.b c", WordUtils.capitalize("a.b c", new char[0]));
}

@org.junit.Test
public void capitalizeFullyHandlesDefaultCustomAndEmptyDelimiters() {
    org.junit.Assert.assertEquals("I Am Fine", WordUtils.capitalizeFully("i AM fINE"));
    org.junit.Assert.assertEquals("I am.Fine",
            WordUtils.capitalizeFully("i AM.fINE", new char[] { '.' }));
    org.junit.Assert.assertEquals("i AM.fINE",
            WordUtils.capitalizeFully("i AM.fINE", new char[0]));
}