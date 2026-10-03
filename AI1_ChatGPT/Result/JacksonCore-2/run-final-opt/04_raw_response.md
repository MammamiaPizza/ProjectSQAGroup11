@org.junit.Test
public void readerParserRejectsLeadingZeroNumbers() throws Exception {
    assertReaderRejects("00");
    assertReaderRejects("-00");
    assertReaderRejects("00.0");
    assertReaderRejects("00e1");
}

@org.junit.Test
public void utf8ParserRejectsLeadingZeroNumbers() throws Exception {
    assertUtf8Rejects("00");
    assertUtf8Rejects("-00");
    assertUtf8Rejects("00.0");
    assertUtf8Rejects("00e1");
}