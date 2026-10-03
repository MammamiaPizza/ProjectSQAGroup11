package org.apache.commons.lang.text;

import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExtendedMessageFormatLang477RegressionTest {

    @Test
    public void escapedQuoteBeforeFormatElementMatchesMessageFormat() {
        String pattern = "''{0}";
        ExtendedMessageFormat format =
                new ExtendedMessageFormat(pattern, Locale.US, new HashMap());

        MessageFormat expected = new MessageFormat(pattern, Locale.US);
        Object[] arguments = new Object[] { "value" };

        assertEquals(expected.toPattern(), format.toPattern());
        assertEquals(expected.format(arguments), format.format(arguments));
    }

    @Test
    public void escapedQuotesAroundFormatElementMatchMessageFormat() {
        String pattern = "before ''{0}'' after";
        ExtendedMessageFormat format =
                new ExtendedMessageFormat(pattern, Locale.US, new HashMap());

        MessageFormat expected = new MessageFormat(pattern, Locale.US);
        Object[] arguments = new Object[] { "value" };

        assertEquals(expected.toPattern(), format.toPattern());
        assertEquals(expected.format(arguments), format.format(arguments));
    }

    @Test
    public void quotedBracesAndEscapedQuoteMatchMessageFormat() {
        String pattern = "quoted '{' and '}' then '' and {0}";
        ExtendedMessageFormat format =
                new ExtendedMessageFormat(pattern, Locale.US, new HashMap());

        MessageFormat expected = new MessageFormat(pattern, Locale.US);
        Object[] arguments = new Object[] { "value" };

        assertEquals(expected.toPattern(), format.toPattern());
        assertEquals(expected.format(arguments), format.format(arguments));
    }

    @Test
    public void customFormatWorksWhenAdjacentToEscapedQuotes() {
        Map registry = new HashMap();
        registry.put("upper", new UpperCaseFormatFactory());

        ExtendedMessageFormat format = new ExtendedMessageFormat(
                "prefix ''{0,upper}'' suffix", Locale.US, registry);

        assertEquals("prefix 'VALUE' suffix",
                format.format(new Object[] { "value" }));
        assertEquals("prefix ''{0,upper}'' suffix", format.toPattern());
    }

    @Test
    public void repeatedApplyPatternWithEscapedQuotesReplacesPreviousPattern() {
        ExtendedMessageFormat format =
                new ExtendedMessageFormat("{0}", Locale.US, new HashMap());

        format.applyPattern("''{0}''");

        MessageFormat expected = new MessageFormat("''{0}''", Locale.US);
        Object[] arguments = new Object[] { "replacement" };

        assertEquals(expected.toPattern(), format.toPattern());
        assertEquals(expected.format(arguments), format.format(arguments));
    }

    @Test(expected = IllegalArgumentException.class)
    public void unterminatedCustomFormatElementIsRejected() {
        Map registry = new HashMap();
        registry.put("upper", new UpperCaseFormatFactory());

        new ExtendedMessageFormat("{0,upper", Locale.US, registry);
    }

    private static final class UpperCaseFormatFactory implements FormatFactory {
        public Format getFormat(String name, String arguments, Locale locale) {
            return new UpperCaseFormat(locale);
        }
    }

    private static final class UpperCaseFormat extends Format {
        private static final long serialVersionUID = 1L;

        private final Locale locale;

        UpperCaseFormat(Locale locale) {
            this.locale = locale;
        }

        public StringBuffer format(Object object, StringBuffer toAppendTo,
                FieldPosition pos) {
            toAppendTo.append(String.valueOf(object).toUpperCase(locale));
            return toAppendTo;
        }

        public Object parseObject(String source, ParsePosition pos) {
            return null;
        }
    }
}