@Test(expected = com.fasterxml.jackson.core.JsonParseException.class)
public void testMangledZeroExponentNoDigitsReader() throws Exception {
    JsonFactory f = new JsonFactory();
    JsonParser p = f.createParser("0e");
    try {
        p.nextToken();
    } finally {
        p.close();
    }
}

@Test(expected = com.fasterxml.jackson.core.JsonParseException.class)
public void testMangledZeroExponentNoDigitsUTF8() throws Exception {
    JsonFactory f = new JsonFactory();
    JsonParser p = f.createParser(new java.io.ByteArrayInputStream("0e".getBytes("UTF-8")));
    try {
        p.nextToken();
    } finally {
        p.close();
    }
}

@Test(expected = com.fasterxml.jackson.core.JsonParseException.class)
public void testMangledTrailingDotExponentReader() throws Exception {
    JsonFactory f = new JsonFactory();
    JsonParser p = f.createParser("1.e");
    try {
        p.nextToken();
    } finally {
        p.close();
    }
}

@Test(expected = com.fasterxml.jackson.core.JsonParseException.class)
public void testMangledTrailingDotExponentUTF8() throws Exception {
    JsonFactory f = new JsonFactory();
    JsonParser p = f.createParser(new java.io.ByteArrayInputStream("1.e".getBytes("UTF-8")));
    try {
        p.nextToken();
    } finally {
        p.close();
    }
}