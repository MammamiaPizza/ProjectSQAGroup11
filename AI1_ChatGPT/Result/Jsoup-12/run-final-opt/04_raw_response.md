@org.junit.Test
public void selectsAttributeValueOperators() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<p id='match' data-key='start-middle-end'></p><p id='other' data-key='different'></p>");

    org.junit.Assert.assertEquals("match",
        org.jsoup.select.Selector.select("[data-key=start-middle-end]", document).get(0).attr("id"));
    org.junit.Assert.assertEquals("match",
        org.jsoup.select.Selector.select("[data-key^=start]", document).get(0).attr("id"));
    org.junit.Assert.assertEquals("match",
        org.jsoup.select.Selector.select("[data-key$=end]", document).get(0).attr("id"));
    org.junit.Assert.assertEquals("match",
        org.jsoup.select.Selector.select("[data-key*=middle]", document).get(0).attr("id"));
}

@org.junit.Test
public void selectsUsingChildDescendantAndSiblingCombinators() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<div id='parent'><span id='first'></span><span id='second'></span><em id='third'></em></div>");

    org.junit.Assert.assertEquals(2,
        org.jsoup.select.Selector.select("#parent > span", document).size());
    org.junit.Assert.assertEquals("third",
        org.jsoup.select.Selector.select("#parent em", document).get(0).attr("id"));
    org.junit.Assert.assertEquals("second",
        org.jsoup.select.Selector.select("#first + span", document).get(0).attr("id"));
    org.junit.Assert.assertEquals("third",
        org.jsoup.select.Selector.select("#first ~ em", document).get(0).attr("id"));
}

@org.junit.Test
public void selectsElementsBySiblingIndexPseudoSelectors() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<ul><li id='zero'></li><li id='one'></li><li id='two'></li><li id='three'></li></ul>");

    org.junit.Assert.assertEquals(2,
        org.jsoup.select.Selector.select("li:lt(2)", document).size());
    org.junit.Assert.assertEquals("one",
        org.jsoup.select.Selector.select("li:eq(1)", document).get(0).attr("id"));
    org.junit.Assert.assertEquals(2,
        org.jsoup.select.Selector.select("li:gt(1)", document).size());
}

@org.junit.Test
public void combinesTagClassAndNotSelectors() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<div id='first' class='alpha beta'></div><div id='second' class='beta'></div><p id='third' class='alpha'></p>");

    org.junit.Assert.assertEquals(2,
        org.jsoup.select.Selector.select(".alpha", document).size());
    org.junit.Assert.assertEquals("first",
        org.jsoup.select.Selector.select("div.alpha", document).get(0).attr("id"));
    org.junit.Assert.assertEquals("second",
        org.jsoup.select.Selector.select("div:not(.alpha)", document).get(0).attr("id"));
}