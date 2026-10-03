@org.junit.Test
public void convenienceParseInitialisesAndRunsTheParser() {
    RecordingTreeBuilder builder = new RecordingTreeBuilder();

    org.jsoup.nodes.Document document = builder.parseDocument("", "http://example.com/");

    org.junit.Assert.assertNotNull(document);
    org.junit.Assert.assertTrue(builder.processCount() > 0);
}

@org.junit.Test
public void startAndEndTagHelpersReturnTheProcessResult() {
    RecordingTreeBuilder builder = new RecordingTreeBuilder();

    builder.setProcessResult(false);
    org.junit.Assert.assertFalse(builder.processStart("div"));
    org.junit.Assert.assertEquals(1, builder.processCount());

    builder.setProcessResult(true);
    org.junit.Assert.assertTrue(builder.processEnd("div"));
    org.junit.Assert.assertEquals(2, builder.processCount());
}

@org.junit.Test
public void currentElementIsNullForEmptyStackAndReturnsStackTop() {
    RecordingTreeBuilder builder = new RecordingTreeBuilder();
    builder.initialiseEmpty();

    org.junit.Assert.assertNull(builder.current());

    org.jsoup.nodes.Document document = builder.pushDocument();
    org.junit.Assert.assertSame(document, builder.current());
}

private static final class RecordingTreeBuilder extends org.jsoup.parser.TreeBuilder {
    private boolean processResult;
    private int processed;

    @Override
    protected boolean process(org.jsoup.parser.Token token) {
        processed++;
        return processResult;
    }

    org.jsoup.nodes.Document parseDocument(String input, String baseUri) {
        return parse(input, baseUri);
    }

    boolean processStart(String name) {
        return processStartTag(name);
    }

    boolean processEnd(String name) {
        return processEndTag(name);
    }

    void initialiseEmpty() {
        initialiseParse("", "", org.jsoup.parser.ParseErrorList.noTracking());
    }

    org.jsoup.nodes.Element current() {
        return currentElement();
    }

    org.jsoup.nodes.Document pushDocument() {
        stack.add(doc);
        return doc;
    }

    void setProcessResult(boolean processResult) {
        this.processResult = processResult;
    }

    int processCount() {
        return processed;
    }
}