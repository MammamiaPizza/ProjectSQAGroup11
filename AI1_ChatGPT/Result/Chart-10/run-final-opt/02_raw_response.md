package org.jfree.chart.imagemap.junit;

import static org.junit.Assert.assertEquals;

import org.jfree.chart.imagemap.StandardToolTipTagFragmentGenerator;
import org.junit.Test;

public class StandardToolTipTagFragmentGeneratorTest {

    @Test
    public void testGenerateToolTipFragmentWithPlainText() {
        StandardToolTipTagFragmentGenerator generator
                = new StandardToolTipTagFragmentGenerator();

        assertEquals(" title=\"Series A, 100.0\" alt=\"\"",
                generator.generateToolTipFragment("Series A, 100.0"));
    }

    @Test
    public void testGenerateToolTipFragmentEscapesDoubleQuotes() {
        StandardToolTipTagFragmentGenerator generator
                = new StandardToolTipTagFragmentGenerator();

        assertEquals(" title=\"Series &quot;A&quot;, 100.0\" alt=\"\"",
                generator.generateToolTipFragment("Series \"A\", 100.0"));
    }

    @Test
    public void testGenerateToolTipFragmentEscapesMultipleDoubleQuotes() {
        StandardToolTipTagFragmentGenerator generator
                = new StandardToolTipTagFragmentGenerator();

        assertEquals(" title=\"&quot;first&quot; and &quot;second&quot;\" alt=\"\"",
                generator.generateToolTipFragment("\"first\" and \"second\""));
    }
}