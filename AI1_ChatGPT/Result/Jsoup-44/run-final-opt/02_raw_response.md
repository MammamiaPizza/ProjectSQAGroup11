package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TreeBuilderBug44Test {

    @Test
    public void invalidTableContentDoesNotDisruptFollowingCommentAndSearchText() {
        Document document = Jsoup.parse(
                "<table><tr><td>Cell</td></tr>misplaced table text</table>"
                        + "<!--after-table-comment-->SearchTextAfterTable");

        String html = document.body().html();
        int commentPosition = html.indexOf("<!--after-table-comment-->");
        int searchPosition = html.indexOf("SearchTextAfterTable");

        assertTrue("Expected the trailing comment to be retained", commentPosition >= 0);
        assertTrue("Expected search text to be retained", searchPosition >= 0);
        assertTrue("Search text did not come after comment", searchPosition > commentPosition);
    }

    @Test
    public void invalidElementInTableDoesNotCorruptLaterCellCommentTextOrder() {
        Document document = Jsoup.parse(
                "<table><tr><td>First</td></tr><div>invalid table content</div>"
                        + "<tr><td><!--cell-comment-->SearchTextInCell</td></tr></table>");

        String html = document.body().html();
        int commentPosition = html.indexOf("<!--cell-comment-->");
        int searchPosition = html.indexOf("SearchTextInCell");

        assertTrue("Expected the cell comment to be retained", commentPosition >= 0);
        assertTrue("Expected cell search text to be retained", searchPosition >= 0);
        assertTrue("Search text did not come after comment", searchPosition > commentPosition);
    }

    @Test
    public void parserContinuesInSourceOrderAfterMalformedTableAndEndTag() {
        Document document = Jsoup.parse(
                "<table><tr><td>One</td></tr><span>invalid</span></table>"
                        + "<!--post-table-comment-->SearchTextPostTable<p>AfterSearch</p>");

        String html = document.body().html();
        int commentPosition = html.indexOf("<!--post-table-comment-->");
        int searchPosition = html.indexOf("SearchTextPostTable");
        int followingParagraphPosition = html.indexOf("AfterSearch");

        assertTrue("Expected the post-table comment to be retained", commentPosition >= 0);
        assertTrue("Expected post-table search text to be retained", searchPosition >= 0);
        assertTrue("Expected following paragraph text to be retained", followingParagraphPosition >= 0);
        assertTrue("Search text did not come after comment", searchPosition > commentPosition);
        assertTrue("Parsing did not continue after search text", followingParagraphPosition > searchPosition);
    }
}