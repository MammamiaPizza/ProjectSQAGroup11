@Test
public void tokenQueueAddFirstRetainsOnlyUnconsumedInput() {
    org.jsoup.parser.TokenQueue queue = new org.jsoup.parser.TokenQueue("abc");
    queue.advance();
    queue.addFirst("XY");

    org.junit.Assert.assertEquals("XYbc", queue.remainder());

    queue.advance();
    queue.addFirst(Character.valueOf('Z'));
    org.junit.Assert.assertEquals("ZYbc", queue.remainder());

    org.jsoup.parser.TokenQueue empty = new org.jsoup.parser.TokenQueue("");
    empty.advance();
    org.junit.Assert.assertTrue(empty.isEmpty());
}

@Test
public void tokenQueueChompsDelimitersAndConsumesAttributeKeys() {
    org.jsoup.parser.TokenQueue queue = new org.jsoup.parser.TokenQueue("before::after");
    org.junit.Assert.assertEquals("before", queue.chompTo("::"));
    org.junit.Assert.assertEquals("after", queue.remainder());

    queue = new org.jsoup.parser.TokenQueue("valueEnDtail");
    org.junit.Assert.assertEquals("value", queue.chompToIgnoreCase("end"));
    org.junit.Assert.assertEquals("tail", queue.remainder());

    queue = new org.jsoup.parser.TokenQueue("data-id:xml=value");
    org.junit.Assert.assertEquals("data-id:xml", queue.consumeAttributeKey());
    org.junit.Assert.assertEquals("=value", queue.remainder());
}

@Test
public void parsesAttributeSelectorsUsingNonEqualityOperators() {
    org.junit.Assert.assertNotNull(org.jsoup.select.QueryParser.parse("[data!=value]"));
    org.junit.Assert.assertNotNull(org.jsoup.select.QueryParser.parse("[data^=prefix]"));
    org.junit.Assert.assertNotNull(org.jsoup.select.QueryParser.parse("[data$=suffix]"));
    org.junit.Assert.assertNotNull(org.jsoup.select.QueryParser.parse("[data*=middle]"));
    org.junit.Assert.assertNotNull(org.jsoup.select.QueryParser.parse("[data~=word]"));
}

@Test
public void parsesDataMatchingAndStructuralPseudoSelectors() {
    org.junit.Assert.assertNotNull(org.jsoup.select.QueryParser.parse(":containsData(payload)"));
    org.junit.Assert.assertNotNull(org.jsoup.select.QueryParser.parse(":matchesOwn(^payload$)"));
    org.junit.Assert.assertNotNull(org.jsoup.select.QueryParser.parse("div:has(span):not(.excluded)"));
}