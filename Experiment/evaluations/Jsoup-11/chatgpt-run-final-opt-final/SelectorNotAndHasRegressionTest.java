package org.jsoup.select;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SelectorNotAndHasRegressionTest {

    @Test
    public void notTagExcludesMatchingElements() {
        Document document = Jsoup.parse("<p id='one'>One</p><div id='box'><span id='leaf'>Leaf</span></div>");
        Element body = document.body();

        Elements matches = Selector.select(":not(p)", body);

        assertEquals(3, matches.size());
        assertTrue(matches.contains(body));
        assertTrue(matches.contains(document.getElementById("box")));
        assertTrue(matches.contains(document.getElementById("leaf")));
        for (Element element : matches) {
            assertFalse(element.tagName().equals("p"));
        }
    }

    @Test
    public void notClassKeepsOnlyDivsWithoutExcludedClass() {
        Document document = Jsoup.parse(
                "<div id='left' class='left'>Left</div>" +
                "<div id='right' class='right'>Right</div>" +
                "<div id='plain'>Plain</div>");

        Elements matches = Selector.select("div:not(.left)", document.body());

        assertEquals(2, matches.size());
        assertEquals("right", matches.get(0).id());
        assertEquals("plain", matches.get(1).id());
    }

    @Test
    public void notAttributeExcludesParagraphWithMatchingAttributeValue() {
        Document document = Jsoup.parse(
                "<p id='1'>First</p>" +
                "<p id='2'>Second</p>" +
                "<p>Third</p>");

        Elements matches = Selector.select("p:not([id=1])", document.body());

        assertEquals(2, matches.size());
        assertEquals("2", matches.get(0).id());
        assertEquals("", matches.get(1).id());
    }

    @Test
    public void hasSelectsEveryAncestorWithMatchingDescendant() {
        Document document = Jsoup.parse("<div id='outer'><span id='inner'><p>Text</p></span></div>");
        Element body = document.body();

        Elements matches = Selector.select(":has(p)", body);

        assertEquals(3, matches.size());
        assertTrue(matches.contains(body));
        assertTrue(matches.contains(document.getElementById("outer")));
        assertTrue(matches.contains(document.getElementById("inner")));
    }

    @Test
    public void nestedTagClassAndAttributeSelectorMatchesExpectedChild() {
        Document document = Jsoup.parse(
                "<section class='card' data-kind='primary'><span id='wanted' class='label'>A</span></section>" +
                "<section class='card' data-kind='secondary'><span class='label'>B</span></section>");

        Elements matches = Selector.select("section.card[data-kind=primary] > span.label", document.body());

        assertEquals(1, matches.size());
        assertEquals("wanted", matches.get(0).id());
    }

    @Test
    public void validSelectorWithNoMatchingElementsReturnsEmptyCollection() {
        Document document = Jsoup.parse("<div class='present'>Text</div>");

        Elements matches = Selector.select("span.missing", document.body());

        assertTrue(matches.isEmpty());
    }

    @Test(expected = Selector.SelectorParseException.class)
    public void unknownPseudoSelectorIsRejected() {
        Document document = Jsoup.parse("<div>Text</div>");

        Selector.select("div:unknown", document.body());
    }

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
}
