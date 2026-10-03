package org.jsoup.parser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HtmlParserSelfClosingFlagTest {

    @Test
    public void selfClosingNonVoidTagReportsSpecificErrorAtTagEnd() {
        ParseErrorList errors = ParseErrorList.tracking(10);

        Parser.parse("<div>abcdef<span/></div>", "", errors);

        assertEquals(1, errors.size());
        assertEquals("18: Tag cannot be self closing; not a void tag", errors.get(0).toString());
    }

    @Test
    public void selfClosingVoidTagDoesNotReportAnError() {
        ParseErrorList errors = ParseErrorList.tracking(10);

        Parser.parse("<div><br/></div>", "", errors);

        assertEquals(0, errors.size());
    }

    @Test
    public void requestedErrorTrackingReportsNonVoidSelfClosingErrorAtCurrentPosition() {
        StringBuilder html = new StringBuilder("<div>");
        for (int i = 0; i < 38; i++) {
            html.append('a');
        }
        html.append("<span/></div>");

        ParseErrorList errors = ParseErrorList.tracking(10);
        Parser.parse(html.toString(), "", errors);

        assertEquals(1, errors.size());
        assertEquals("50: Tag cannot be self closing; not a void tag", errors.get(0).toString());
    }
}
