@Test
public void matchTextSelectsOnlyDirectTextWhenInlineElementsArePresent() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<p>One<span>Two</span>Three</p>");

    org.jsoup.select.Elements matches = document.select("p:matchText");

    org.junit.Assert.assertEquals(2, matches.size());
    org.junit.Assert.assertEquals("One", matches.get(0).text());
    org.junit.Assert.assertEquals("Three", matches.get(1).text());
    org.junit.Assert.assertSame(document.selectFirst("p"), matches.get(0).parent());
    org.junit.Assert.assertSame(document.selectFirst("p"), matches.get(1).parent());
}

@Test
public void matchTextCanMatchInheritedParentClass() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<p class='note'>One<br>Two</p><p>Three</p>");

    org.jsoup.select.Elements matches = document.select("p.note:matchText");

    org.junit.Assert.assertEquals(2, matches.size());
    org.junit.Assert.assertEquals("One", matches.get(0).text());
    org.junit.Assert.assertEquals("Two", matches.get(1).text());
    org.junit.Assert.assertEquals("note", matches.get(0).className());
    org.junit.Assert.assertEquals("note", matches.get(1).className());
}

@Test
public void matchTextCanBeChainedWithLastChild() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<p>One<br>Two<br>Three</p>");

    org.jsoup.select.Elements matches = document.select("p:matchText:last-child");

    org.junit.Assert.assertEquals(1, matches.size());
    org.junit.Assert.assertEquals("Three", matches.first().text());
}

@Test
public void matchTextPseudoElementsSerializeAsTheirTextOnly() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<p>One<br>Two</p>");

    org.jsoup.select.Elements matches = document.select("p:matchText");

    org.junit.Assert.assertEquals("One", matches.get(0).outerHtml());
    org.junit.Assert.assertEquals("Two", matches.get(1).outerHtml());
    org.junit.Assert.assertEquals("<p>One<br>Two</p>", document.selectFirst("p").outerHtml());
}