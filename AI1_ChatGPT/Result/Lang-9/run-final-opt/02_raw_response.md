package org.apache.commons.lang3.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

public class FastDateParserLang832Test {

    private FastDateParser parser() {
        return new FastDateParser("d'd'", TimeZone.getTimeZone("UTC"), Locale.US);
    }

    private Date date(int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
        calendar.clear();
        calendar.set(1970, month, dayOfMonth);
        return calendar.getTime();
    }

    @Test
    public void parsesNumericDayWhenRequiredQuotedLiteralFollows() throws Exception {
        assertEquals(date(Calendar.JANUARY, 3), parser().parse("3d"));
    }

    @Test
    public void parsesMultiDigitDayBeforeRequiredQuotedLiteral() throws Exception {
        assertEquals(date(Calendar.FEBRUARY, 2), parser().parse("33d"));
    }

    @Test
    public void parsesUnicodeDecimalDigitsBeforeRequiredQuotedLiteral() throws Exception {
        assertEquals(date(Calendar.FEBRUARY, 2), parser().parse("\u0663\u0663d"));
    }

    @Test
    public void parseStringRejectsLiteralBeforeNumericDay() throws Exception {
        try {
            parser().parse("d3");
            fail("A literal preceding the numeric day must not match pattern d'd'");
        } catch (ParseException expected) {
            assertEquals(0, expected.getErrorOffset());
        }
    }

    @Test
    public void parsePositionReturnsNullForLiteralBeforeUnicodeNumericDay() {
        ParsePosition position = new ParsePosition(0);

        Date parsed = parser().parse("d\u0663", position);

        assertNull(parsed);
        assertEquals(0, position.getIndex());
    }

    @Test
    public void parseStringRejectsMissingRequiredLiteral() throws Exception {
        try {
            parser().parse("3");
            fail("The quoted literal must be present after the numeric day");
        } catch (ParseException expected) {
            assertEquals(0, expected.getErrorOffset());
        }
    }
}