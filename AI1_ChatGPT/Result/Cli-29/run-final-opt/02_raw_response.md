package org.apache.commons.cli;

import junit.framework.TestCase;

public class UtilCli29Test extends TestCase
{
    public void testRemovesMatchingLeadingAndTrailingQuotes()
    {
        assertEquals("one two", Util.stripLeadingAndTrailingQuotes("\"one two\""));
    }

    public void testRemovesQuotesFromEmptyQuotedString()
    {
        assertEquals("", Util.stripLeadingAndTrailingQuotes("\"\""));
    }

    public void testPreservesStringWithoutQuotes()
    {
        assertEquals("plain value", Util.stripLeadingAndTrailingQuotes("plain value"));
    }

    public void testPreservesOnlyLeadingQuote()
    {
        assertEquals("\"leading only", Util.stripLeadingAndTrailingQuotes("\"leading only"));
    }

    public void testPreservesOnlyTrailingQuote()
    {
        assertEquals("trailing only\"", Util.stripLeadingAndTrailingQuotes("trailing only\""));
    }

    public void testPreservesUnmatchedQuoteAtEndOfEmbeddedQuotedContent()
    {
        assertEquals("foo \"bar\"", Util.stripLeadingAndTrailingQuotes("foo \"bar\""));
    }

    public void testPreservesSingleQuote()
    {
        assertEquals("\"", Util.stripLeadingAndTrailingQuotes("\""));
    }

    public void testPreservesEmptyString()
    {
        assertEquals("", Util.stripLeadingAndTrailingQuotes(""));
    }
}