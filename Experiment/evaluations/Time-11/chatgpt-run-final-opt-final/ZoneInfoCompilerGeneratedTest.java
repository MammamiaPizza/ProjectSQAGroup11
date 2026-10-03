package org.joda.time.tz;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Map;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class ZoneInfoCompilerGeneratedTest extends TestCase {

    public void testRecurringRulesApplyAtUtcLastSundayTransitions() throws Exception {
        String data =
            "# recurring daylight saving rules\n" +
            "Rule Test 2000 max - Mar lastSun 1:00u 1:00 D\n" +
            "Rule Test 2000 max - Oct lastSun 1:00u 0 S\n" +
            "Zone Test/Recurring 0:00 Test T%sT # trailing comment\n";

        DateTimeZone zone = compile(data).get("Test/Recurring");

        long springTransition = utc(2001, 3, 25, 1, 0);
        long autumnTransition = utc(2001, 10, 28, 1, 0);

        assertEquals(0, zone.getOffset(springTransition - 1));
        assertEquals("TST", zone.getNameKey(springTransition - 1));
        assertEquals(60 * 60 * 1000, zone.getOffset(springTransition));
        assertEquals("TDT", zone.getNameKey(springTransition));
        assertEquals(springTransition, zone.nextTransition(springTransition - 1));
        assertEquals(springTransition - 1, zone.previousTransition(springTransition));

        assertEquals(60 * 60 * 1000, zone.getOffset(autumnTransition - 1));
        assertEquals("TDT", zone.getNameKey(autumnTransition - 1));
        assertEquals(0, zone.getOffset(autumnTransition));
        assertEquals("TST", zone.getNameKey(autumnTransition));
        assertEquals(autumnTransition, zone.nextTransition(autumnTransition - 1));
        assertEquals(autumnTransition - 1, zone.previousTransition(autumnTransition));

        assertTrue(ZoneInfoCompiler.test(zone.getID(), zone));
    }

    public void testLinkAliasesReferToCompiledZone() throws Exception {
        String data =
            "Rule AliasRules 2000 max - Mar lastSun 1:00u 1:00 D\n" +
            "Rule AliasRules 2000 max - Oct lastSun 1:00u 0 S\n" +
            "Zone Test/Original 0:00 AliasRules A%sT\n" +
            "Link Test/Original Test/Alias\n";

        Map<String, DateTimeZone> zones = compile(data);
        DateTimeZone original = zones.get("Test/Original");
        DateTimeZone alias = zones.get("Test/Alias");
        long instant = utc(2002, 7, 1, 12, 0);

        assertNotNull(original);
        assertNotNull(alias);
        assertSame(original, alias);
        assertEquals(original.getID(), alias.getID());
        assertEquals(original.getOffset(instant), alias.getOffset(instant));
        assertEquals("ADT", alias.getNameKey(instant));
    }

    public void testZoneUntilCutoverChangesOffsetAndNameAtSpecifiedUtcInstant()
        throws Exception {
        String data =
            "Zone Test/Cutover 0:00 - AAA 2005 Mar lastSun 1:00u\n" +
            "    2:00 - BBB\n";

        DateTimeZone zone = compile(data).get("Test/Cutover");
        long cutover = utc(2005, 3, 27, 1, 0);

        assertEquals(0, zone.getOffset(cutover - 1));
        assertEquals("AAA", zone.getNameKey(cutover - 1));
        assertEquals(2 * 60 * 60 * 1000, zone.getOffset(cutover));
        assertEquals("BBB", zone.getNameKey(cutover));
        assertEquals(cutover, zone.nextTransition(cutover - 1));
        assertEquals(cutover - 1, zone.previousTransition(cutover));
        assertTrue(ZoneInfoCompiler.test(zone.getID(), zone));
    }

    public void testUntilCutoverStopsRecurringRulesAndUsesFollowingFixedSegment()
        throws Exception {
        String data =
            "Rule UntilRules 2000 max - Mar lastSun 1:00u 1:00 D\n" +
            "Rule UntilRules 2000 max - Oct lastSun 1:00u 0 S\n" +
            "Zone Test/Until 0:00 UntilRules U%sT 2005 Jan 1 0:00u\n" +
            "    2:00 - FIX\n";

        DateTimeZone zone = compile(data).get("Test/Until");
        long cutover = utc(2005, 1, 1, 0, 0);
        long afterFormerSpringRule = utc(2005, 3, 27, 2, 0);

        assertEquals(0, zone.getOffset(cutover - 1));
        assertEquals(2 * 60 * 60 * 1000, zone.getOffset(cutover));
        assertEquals("FIX", zone.getNameKey(cutover));
        assertEquals(cutover, zone.nextTransition(cutover - 1));
        assertEquals(2 * 60 * 60 * 1000, zone.getOffset(afterFormerSpringRule));
        assertEquals("FIX", zone.getNameKey(afterFormerSpringRule));
        assertTrue(ZoneInfoCompiler.test(zone.getID(), zone));
    }

    private Map<String, DateTimeZone> compile(String data) throws Exception {
        ZoneInfoCompiler compiler = new ZoneInfoCompiler();
        compiler.parseDataFile(new BufferedReader(new StringReader(data)));
        return compiler.compile(null, null);
    }

    private long utc(int year, int month, int day, int hour, int minute) {
        return new DateTime(year, month, day, hour, minute, DateTimeZone.UTC).getMillis();
    }
}
