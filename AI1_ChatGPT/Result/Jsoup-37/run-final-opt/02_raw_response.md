package org.jsoup.nodes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ElementNotPrettyPrintTest {

    @Test
    public void htmlDoesNotInsertWhitespaceBetweenBlockChildrenWhenPrettyPrintingIsDisabled() {
        Document document = Jsoup.parse("<div><p>One</p><p>Two</p></div>");
        document.outputSettings().prettyPrint(false);

        assertEquals("<div><p>One</p><p>Two</p></div>", document.body().html());
    }

    @Test
    public void elementToStringDoesNotPrettyPrintNestedBlockElementsWhenDisabled() {
        Document document = Jsoup.parse("<div><section><p>One <span>two</span></p><p>Three</p></section></div>");
        document.outputSettings().prettyPrint(false);
        Element div = document.body().child(0);

        assertEquals("<div><section><p>One <span>two</span></p><p>Three</p></section></div>", div.toString());
    }

    @Test
    public void htmlPreservesCompactEmptyElementsWhenPrettyPrintingIsDisabled() {
        Document document = Jsoup.parse("<div><p></p><p></p></div>");
        document.outputSettings().prettyPrint(false);

        assertEquals("<div><p></p><p></p></div>", document.body().html());
    }

    @Test
    public void htmlAndToStringUseTheSameCompactSerializationForAnElementWhenDisabled() {
        Document document = Jsoup.parse("<div><p>One</p><p>Two</p></div>");
        document.outputSettings().prettyPrint(false);
        Element div = document.body().child(0);

        assertEquals("<p>One</p><p>Two</p>", div.html());
        assertEquals("<div><p>One</p><p>Two</p></div>", div.toString());
    }
}