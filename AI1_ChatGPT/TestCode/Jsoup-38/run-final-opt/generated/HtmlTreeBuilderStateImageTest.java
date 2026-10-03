package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HtmlTreeBuilderStateImageTest {

    @Test
    public void convertsImageStartTagToImgElement() {
        Document document = Jsoup.parse("<image>");
        assertEquals("<img />", document.body().html());
    }

    @Test
    public void convertsImageStartTagAndPreservesAttributes() {
        Document document = Jsoup.parse("<image src='photo.png' alt='A photo'>");
        assertEquals("<img src=\"photo.png\" alt=\"A photo\" />", document.body().html());
    }

    @Test
    public void handlesNormalImgStartTagAsVoidElement() {
        Document document = Jsoup.parse("<img src='photo.png'>");
        assertEquals("<img src=\"photo.png\" />", document.body().html());
    }

    @Test
    public void convertsMixedCaseImageStartTagToImgElement() {
        Document document = Jsoup.parse("<IMAGE>");
        assertEquals("<img />", document.body().html());
    }
}
