package org.joda.time.tz;

 import java.io.*;
 import java.util.*;

 import junit.framework.TestCase;

 import org.joda.time.*;
 import org.joda.time.tz.DateTimeZoneBuilder;

 /**
  * Tests targeting ZoneInfoCompiler bug #18: incorrect handling of negative DST savings.
  * Designed to fail on the buggy version and pass on the fixed version.
  * Covers normal, boundary, exception, and fault-related behaviors.
  */
 public class TestCompilerBug18 extends TestCase {

     // Helper: compiles a zoneinfo file from the given data string and returns the map.
     private Map<String, DateTimeZone> compileFromString(String data) throws IOException {
         ZoneInfoCompiler compiler = new ZoneInfoCompiler();
         BufferedReader reader = new BufferedReader(new StringReader(data));
         compiler.parseDataFile(reader);
         return compiler.compile(null, new File[0]);
     }

     // 1. Simple zone without any DST rules
     public void testCompileSimpleZoneNoDST() throws Exception {
         String data = "Zone UTC-0 0:00 - UTC";
         Map<String, DateTimeZone> map = compileFromString(data);
         assertTrue("Zone UTC-0 is missing", map.containsKey("UTC-0"));
         DateTimeZone tz = map.get("UTC-0");
         long instant = 0L;
         assertEquals("Offset should be 0", 0, tz.getOffset(instant));
         assertEquals("Standard offset should be 0", 0, tz.getStandardOffset(instant));
     }

     // 2. Zone with positive DST (standard case – should pass even on buggy version)
     public void testCompilePositiveDST() throws Exception {
         String data =
             "Rule Pos 2010 only - Apr 1 2:00 1:00 D\n" +
             "Rule Pos 2010 only - Oct 1 2:00 0 S\n" +
             "Zone Test/Pos 1:00 Pos EST/EDT";
         Map<String, DateTimeZone> map = compileFromString(data);
         assertTrue(map.containsKey("Test/Pos"));
         DateTimeZone tz = map.get("Test/Pos");
         long jan = new DateTime(2010, 1, 1, 0, 0, 0, tz).getMillis();
         long jun = new DateTime(2010, 6, 1, 0, 0, 0, tz).getMillis();
         // Standard offset = 1 hour (3600000 ms), DST adds 1 hour → 2 hours
         assertEquals(3600000, tz.getOffset(jan));
         assertEquals(7200000, tz.getOffset(jun));
     }

     // 3. Zone with negative DST savings – core bug exposure
     public void testCompileNegativeDST() throws Exception {
         String data =
             "Rule Neg 2010 only - Apr 1 2:00 -1:00 D\n" +
             "Rule Neg 2010 only - Oct 1 2:00 0 S\n" +
             "Zone Test/Neg 3:00 Neg NEG";
         Map<String, DateTimeZone> map = compileFromString(data);
         assertTrue("Zone Test/Neg must be present", map.containsKey("Test/Neg"));
         DateTimeZone tz = map.get("Test/Neg");
         long jan = new DateTime(2010, 1, 1, 0, 0, 0, tz).getMillis();
         long jun = new DateTime(2010, 6, 1, 0, 0, 0, tz).getMillis();
         // Standard offset 3 hours, DST savings -1 hour → total offset 2 hours in summer
         assertEquals("Winter offset", 3 * 3600000, tz.getOffset(jan));
         assertEquals("Summer offset with negative savings", 2 * 3600000, tz.getOffset(jun));
     }

     // 4. Negative DST savings with minutes and larger magnitude
     public void testCompileNegativeDSTMinutes() throws Exception {
         String data =
             "Rule NegM 2010 only - May 15 2:00 -2:30 D\n" +
             "Rule NegM 2010 only - Sep 15 2:00 0 S\n" +
             "Zone Test/NegM 4:00 NegM NEGM";
         Map<String, DateTimeZone> map = compileFromString(data);
         assertTrue(map.containsKey("Test/NegM"));
         DateTimeZone tz = map.get("Test/NegM");
         long mar = new DateTime(2010, 3, 1, 0, 0, 0, tz).getMillis();
         long aug = new DateTime(2010, 8, 1, 0, 0, 0, tz).getMillis();
         // Standard offset 4:00, DST savings -2:30 → DST offset 1:30 (5400000 ms)
         assertEquals(4 * 3600000, tz.getOffset(mar));
         assertEquals(1 * 3600000 + 30 * 60000, tz.getOffset(aug));
     }

     // 5. Leap-day transition (Feb 29) – ensures correct handling of the day of month
     public void testCompileLeapDayTransition() throws Exception {
         String data =
             "Rule Leap 2012 only - Feb 29 2:00 1:00 D\n" +
             "Rule Leap 2012 only - Oct 29 2:00 0 S\n" +
             "Zone Test/Leap 2:00 Leap LEP";
         Map<String, DateTimeZone> map = compileFromString(data);
         assertTrue(map.containsKey("Test/Leap"));
         DateTimeZone tz = map.get("Test/Leap");

         // Standard offset is +2 hours; transition at local 2:00 is 0:00 UTC
         long utcTransition = new DateTime(2012, 2, 29, 0, 0, 0, DateTimeZone.UTC).getMillis();
         long before = utcTransition - 1000; // 1 second before
         long next = tz.nextTransition(before);
         assertEquals("Transition should happen at 2012-02-29T02:00 local", utcTransition, next);

         // After transition offset should be standard +1 hour = 3 hours
         assertEquals(2 * 3600000, tz.getOffset(before));
         assertEquals(3 * 3600000, tz.getOffset(next + 1000));
     }

     // 6. Large positive years (far future)
     public void testCompileBoundaryLargeYears() throws Exception {
         String data =
             "Rule Large 2900 only - Jan 1 0:00 1:00 D\n" +
             "Rule Large 2900 only - Dec 31 0:00 0 S\n" +
             "Zone Test/Large 5:00 Large LRG";
         Map<String, DateTimeZone> map = compileFromString(data);
         assertTrue(map.containsKey("Test/Large"));
         DateTimeZone tz = map.get("Test/Large");
         long feb = new DateTime(2900, 2, 1, 0, 0, 0, tz).getMillis();
         // DST in effect: offset = 5+1 = 6 hours
         assertEquals(6 * 3600000, tz.getOffset(feb));
     }

     // 7. Negative years (BC)
     public void testCompileBoundaryNegativeYears() throws Exception {
         String data =
             "Rule NegY -10 only - Jul 1 0:00 2:00 D\n" +
             "Rule NegY -10 only - Nov 1 0:00 0 S\n" +
             "Zone Test/NegY 1:00 NegY NY";
         Map<String, DateTimeZone> map = compileFromString(data);
         assertTrue(map.containsKey("Test/NegY"));
         DateTimeZone tz = map.get("Test/NegY");
         // Year -10 is 11 BC; pick a date safely inside the DST period
         long aug = new DateTime(-10, 8, 1, 0, 0, 0, tz).getMillis();
         assertEquals(1 * 3600000 + 2 * 3600000, tz.getOffset(aug)); // 3 hours
     }

     // 8. Rule referenced but not defined – zone should fall back to standard offset without error
     public void testCompileMissingRule() throws Exception {
         String data = "Zone Test/Missing 8:00 NoSuchRule MISS";
         Map<String, DateTimeZone> map = compileFromString(data);
         assertTrue("Zone should still be created even if rule is missing",
             map.containsKey("Test/Missing"));
         DateTimeZone tz = map.get("Test/Missing");
         long now = System.currentTimeMillis();
         assertEquals("Offset should be the standard offset only", 8 * 3600000, tz.getOffset(now));
     }

     // 9. Empty rule set – rules list present but contains no active rules
     public void testCompileEmptyRuleSet() throws Exception {
         String data =
             "Rule Empty 3000 3000 - Jan 1 0:00 1:00 D\n" +
             "Zone Test/Empty 2:00 Empty EMP";
         Map<String, DateTimeZone> map = compileFromString(data);
         assertTrue(map.containsKey("Test/Empty"));
         DateTimeZone tz = map.get("Test/Empty");
         long now = 0L;
         assertEquals(2 * 3600000, tz.getOffset(now)); // always standard
     }

     // 10. Malformed line – should be ignored without crashing
     public void testCompileMalformedLine() throws Exception {
         String data =
             "Zone Test/Mal 5:00 - MAL\n" +
             "garbage\n" +
             "Rule Bad\n"; // incomplete
         Map<String, DateTimeZone> map = compileFromString(data);
         assertTrue("Well-formed zone must still be created", map.containsKey("Test/Mal"));
         DateTimeZone tz = map.get("Test/Mal");
         assertEquals(5 * 3600000, tz.getOffset(0L));
     }

     // 11. Direct use of DateTimeOfYear.addCutover via builder
     public void testAddCutoverDirect() throws Exception {
         DateTimeZoneBuilder builder = new DateTimeZoneBuilder();
         builder.setStandardOffset(3600000);
         // February 28, non-leap year, at midnight standard time
         ZoneInfoCompiler.DateTimeOfYear dtoy =
             new ZoneInfoCompiler.DateTimeOfYear(2, 28, 0, false, 0, 'w');
         dtoy.addCutover(builder, 2015);
         DateTimeZone tz = builder.toDateTimeZone("Test/Cutover", false);
         assertNotNull(tz);
         long before = new DateTime(2015, 2, 27, 23, 0, 0, tz).getMillis();
         long after = new DateTime(2015, 3, 1, 0, 0, 0, tz).getMillis();
         assertEquals(3600000, tz.getOffset(before));
         assertEquals(3600000, tz.getOffset(after));
     }

     // 12. Coverage of Zone.buildDateTimeZone via the compile pipeline
     public void testBuildDateTimeZone() throws Exception {
         // The previous test methods already exercise buildDateTimeZone inside compile(),
         // but here we specifically verify that a zone built from parsed data behaves correctly.
         String data =
             "Rule Bld 2020 only - Jun 1 0:00 0:30 D\n" +
             "Rule Bld 2020 only - Sep 1 0:00 0 S\n" +
             "Zone Test/Bld 5:00 Bld BLD";
         Map<String, DateTimeZone> map = compileFromString(data);
         DateTimeZone tz = map.get("Test/Bld");
         long mar = new DateTime(2020, 3, 1, 0, 0, 0, tz).getMillis();
         long jul = new DateTime(2020, 7, 1, 0, 0, 0, tz).getMillis();
         assertEquals(5 * 3600000, tz.getOffset(mar));
         assertEquals(5 * 3600000 + 30 * 60000, tz.getOffset(jul));
         // Check that name key changes across transition
         assertTrue(!tz.getNameKey(mar).equals(tz.getNameKey(jul)));
     }
 }