@org.junit.Test
public void supportsAllAttributeComparisonOperators() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<div id='matching' data-value='prefix-middle-suffix'></div>" +
        "<div id='different' data-value='other'></div>");

    org.junit.Assert.assertEquals(2, document.select("[data-value]").size());
    org.junit.Assert.assertEquals("matching", document.select("[data-value=prefix-middle-suffix]").first().id());
    org.junit.Assert.assertEquals("different", document.select("[data-value!=prefix-middle-suffix]").first().id());
    org.junit.Assert.assertEquals("matching", document.select("[data-value^=prefix]").first().id());
    org.junit.Assert.assertEquals("matching", document.select("[data-value$=suffix]").first().id());
    org.junit.Assert.assertEquals("matching", document.select("[data-value*=middle]").first().id());
    org.junit.Assert.assertEquals("matching", document.select("[data-value~=^prefix.*suffix$]").first().id());
}

@org.junit.Test
public void supportsIndexPseudoSelectorsAndUniversalSelector() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<ul><li id='a'></li><li id='b'></li><li id='c'></li><li id='d'></li></ul>");

    org.junit.Assert.assertEquals(4, document.select("ul > *").size());
    org.junit.Assert.assertEquals(2, document.select("li:lt(2)").size());
    org.junit.Assert.assertEquals(2, document.select("li:gt(1)").size());
    org.junit.Assert.assertEquals("c", document.select("li:eq(2)").first().id());
}

@org.junit.Test
public void supportsContentAndNestedPseudoSelectors() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<div id='parent'>own words<span id='child'>needle</span></div>" +
        "<div id='other'>different</div>");

    org.junit.Assert.assertEquals("parent", document.select("div:has(span)").first().id());
    org.junit.Assert.assertEquals("parent", document.select("div:contains(needle)").first().id());
    org.junit.Assert.assertEquals("parent", document.select("div:containsOwn(own words)").first().id());
    org.junit.Assert.assertEquals("parent", document.select("div:matches(needle)").first().id());
    org.junit.Assert.assertEquals("parent", document.select("div:matchesOwn(^own words$)").first().id());
    org.junit.Assert.assertEquals("other", document.select("div:not(:has(span))").first().id());
}

@org.junit.Test
public void supportsAdjacentAndGeneralSiblingCombinators() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<div><p id='a'></p><p id='b'></p><span id='c'></span><p id='d'></p></div>");

    org.junit.Assert.assertEquals("b", document.select("p+p").first().id());
    org.junit.Assert.assertEquals(2, document.select("p~p").size());
    org.junit.Assert.assertEquals("b", document.select("p~p").get(0).id());
    org.junit.Assert.assertEquals("d", document.select("p~p").get(1).id());
}