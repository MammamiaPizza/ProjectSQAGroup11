package org.jsoup.select;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QueryParserCommaGroupingTest {

    @Test
    public void selectsEachCompleteClauseWhenCommaFollowsChildCombinator() {
        Document document = Jsoup.parse(
                "<div><p id='inside'>inside</p></div>" +
                "<span id='marker'>marker</span>" +
                "<p id='outside'>outside</p>");

        Elements matches = document.select("div > p, span");

        assertEquals(2, matches.size());
        assertEquals("inside", matches.get(0).id());
        assertEquals("marker", matches.get(1).id());
    }

    @Test
    public void commaGroupsKeepAndConditionsWithinEachClause() {
        Document document = Jsoup.parse(
                "<p id='alpha' class='alpha'>one</p>" +
                "<p id='beta' class='beta'>two</p>" +
                "<p id='plain'>three</p>");

        Elements matches = document.select("p.alpha, p.beta");

        assertEquals(2, matches.size());
        assertEquals("alpha", matches.get(0).id());
        assertEquals("beta", matches.get(1).id());
    }

    @Test
    public void commaInsideMatchesRegularExpressionIsNotATopLevelGroupSeparator() {
        Document document = Jsoup.parse(
                "<p id='a'>a</p>" +
                "<p id='b'>b</p>" +
                "<p id='c'>c</p>");

        Elements matches = document.select("p.missing, p:matches([ab,])");

        assertEquals(2, matches.size());
        assertEquals("a", matches.get(0).id());
        assertEquals("b", matches.get(1).id());
    }

    @Test
    public void commaInsideAttributeRegularExpressionIsNotATopLevelGroupSeparator() {
        Document document = Jsoup.parse(
                "<p id='a' data-value='a'>a</p>" +
                "<p id='b' data-value='b'>b</p>" +
                "<p id='c' data-value='c'>c</p>");

        Elements matches = document.select("p.missing, p[data-value~=[ab,]]");

        assertEquals(2, matches.size());
        assertEquals("a", matches.get(0).id());
        assertEquals("b", matches.get(1).id());
    }

    @Test
    public void commaInsideHasSelectorIsNotATopLevelGroupSeparator() {
        Document document = Jsoup.parse(
                "<div id='target'><span>child</span></div>" +
                "<div id='other'><strong>other</strong></div>");

        Elements matches = document.select("div.missing, div:has(span, em)");

        assertEquals(1, matches.size());
        assertEquals("target", matches.get(0).id());
    }
}
