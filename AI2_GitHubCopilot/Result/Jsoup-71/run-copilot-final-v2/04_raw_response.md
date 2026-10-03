@Test
public void testAttributeEndsWithSelector() {
    Evaluator eval = QueryParser.parse("[id$=test]");
    assertNotNull(eval);
}

@Test
public void testAttributeMatchesPatternSelector() {
    Evaluator eval = QueryParser.parse("[class~=regex]");
    assertNotNull(eval);
}

@Test
public void testAllElementsSelectorParse() {
    Evaluator eval = QueryParser.parse("*");
    assertNotNull(eval);
}

@Test(expected = org.jsoup.select.Selector.SelectorParseException.class)
public void testAttributeInvalidOperatorThrows() {
    QueryParser.parse("[attr!val]");
}