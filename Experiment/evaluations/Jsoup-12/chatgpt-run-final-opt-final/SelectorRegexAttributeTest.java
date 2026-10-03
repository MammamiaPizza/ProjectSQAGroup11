package org.jsoup.select;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SelectorRegexAttributeTest {

    @Test
    public void selectsAttributeValuesMatchingEitherRegexAlternative() {
        Document document = Jsoup.parse(
            "<div>" +
                "<p id='x' data-value='x'>x</p>" +
                "<p id='y' data-value='y'>y</p>" +
                "<p id='z' data-value='z'>z</p>" +
            "</div>");

        Elements matches = Selector.select("p[data-value~=x|y]", document);

        assertEquals(2, matches.size());
        assertEquals("x", matches.get(0).attr("id"));
        assertEquals("y", matches.get(1).attr("id"));
    }

    @Test
    public void combinesAttributeRegexWithClassSelector() {
        Document document = Jsoup.parse(
            "<div>" +
                "<p id='x' class='selected' data-value='x'>x</p>" +
                "<p id='y' data-value='y'>y</p>" +
                "<p id='z' class='selected' data-value='z'>z</p>" +
            "</div>");

        Elements matches = Selector.select("p.selected[data-value~=x|y]", document);

        assertEquals(1, matches.size());
        assertEquals("x", matches.get(0).attr("id"));
    }

    @Test
    public void continuesParsingSelectorsAfterAttributeRegex() {
        Document document = Jsoup.parse(
            "<div>" +
                "<p id='x' class='selected' data-value='x'>x</p>" +
                "<p id='y' class='selected' data-value='y'>y</p>" +
                "<p id='z' class='selected' data-value='z'>z</p>" +
            "</div>");

        Elements matches = Selector.select("p[data-value~=x|y].selected", document);

        assertEquals(2, matches.size());
        assertEquals("x", matches.get(0).attr("id"));
        assertEquals("y", matches.get(1).attr("id"));
    }

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
}
