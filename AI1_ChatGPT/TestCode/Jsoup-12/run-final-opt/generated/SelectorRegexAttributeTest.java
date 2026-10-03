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
}
