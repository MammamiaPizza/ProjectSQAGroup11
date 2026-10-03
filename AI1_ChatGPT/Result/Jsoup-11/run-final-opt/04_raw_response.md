@Test
public void attributeValueOperatorsSelectMatchingElements() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<div id='start' data-value='prefix-middle-suffix'></div>"
            + "<div id='other' data-value='unrelated'></div>");

    assertEquals("start", document.select("[data-value^=prefix]").get(0).id());
    assertEquals("start", document.select("[data-value$=suffix]").get(0).id());
    assertEquals("start", document.select("[data-value*=middle]").get(0).id());
    assertEquals("start", document.select("[data-value~=prefix-.+-suffix]").get(0).id());
}

@Test
public void indexPseudoSelectorsUseSiblingIndexes() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<ul><li id='zero'></li><li id='one'></li><li id='two'></li></ul>");

    org.jsoup.select.Elements lessThan = document.select("li:lt(2)");
    assertEquals(2, lessThan.size());
    assertEquals("zero", lessThan.get(0).id());
    assertEquals("one", lessThan.get(1).id());

    org.jsoup.select.Elements greaterThan = document.select("li:gt(0)");
    assertEquals(2, greaterThan.size());
    assertEquals("one", greaterThan.get(0).id());
    assertEquals("two", greaterThan.get(1).id());

    org.jsoup.select.Elements equalTo = document.select("li:eq(1)");
    assertEquals(1, equalTo.size());
    assertEquals("one", equalTo.get(0).id());
}

@Test
public void siblingCombinatorsSelectOnlyFollowingSiblings() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<section><div id='first'></div><div id='second'></div><div id='third'></div></section>");

    org.jsoup.select.Elements adjacent = document.select("div + div");
    assertEquals(2, adjacent.size());
    assertEquals("second", adjacent.get(0).id());
    assertEquals("third", adjacent.get(1).id());

    org.jsoup.select.Elements general = document.select("#first ~ div");
    assertEquals(2, general.size());
    assertEquals("second", general.get(0).id());
    assertEquals("third", general.get(1).id());
}