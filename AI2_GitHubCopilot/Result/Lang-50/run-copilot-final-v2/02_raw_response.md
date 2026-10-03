package org.apache.commons.lang.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FastDateFormatLocaleCacheTest {

 private Locale originalDefault;

 @Before
 public void setUp() {
     originalDefault = Locale.getDefault();
 }

 @After
 public void tearDown() {
     Locale.setDefault(originalDefault);
 }

 @Test
 public void testGetDateInstanceDefaultLocaleChange() {
     Locale.setDefault(Locale.GERMANY);
     FastDateFormat fmt1 = FastDateFormat.getDateInstance(FastDateFormat.FULL);
     assertEquals("First instance must use current default locale", Locale.GERMANY,
fmt1.getLocale());

     Locale.setDefault(Locale.US);
     FastDateFormat fmt2 = FastDateFormat.getDateInstance(FastDateFormat.FULL);
     assertEquals("After locale change, new instance must reflect new default", Locale.US,
fmt2.getLocale());
     assertNotSame("Caching must not return stale instance for different locale", fmt1, fmt2);
 }

 @Test
 public void testGetTimeInstanceDefaultLocaleChange() {
     Locale.setDefault(Locale.GERMANY);
     FastDateFormat fmt1 = FastDateFormat.getTimeInstance(FastDateFormat.MEDIUM);
     assertEquals(Locale.GERMANY, fmt1.getLocale());

     Locale.setDefault(Locale.US);
     FastDateFormat fmt2 = FastDateFormat.getTimeInstance(FastDateFormat.MEDIUM);
     assertEquals(Locale.US, fmt2.getLocale());
     assertNotSame(fmt1, fmt2);
 }

 @Test
 public void testGetDateTimeInstanceDefaultLocaleChange() {
     Locale.setDefault(Locale.GERMANY);
     FastDateFormat fmt1 = FastDateFormat.getDateTimeInstance(FastDateFormat.LONG,
FastDateFormat.SHORT);
     assertEquals(Locale.GERMANY, fmt1.getLocale());

     Locale.setDefault(Locale.US);
     FastDateFormat fmt2 = FastDateFormat.getDateTimeInstance(FastDateFormat.LONG,
FastDateFormat.SHORT);
     assertEquals(Locale.US, fmt2.getLocale());
     assertNotSame(fmt1, fmt2);
 }

 @Test
 public void testGetInstanceNoArgsDefaultLocaleChange() {
     Locale.setDefault(Locale.GERMANY);
     FastDateFormat fmt1 = FastDateFormat.getInstance();
     assertEquals(Locale.GERMANY, fmt1.getLocale());

     Locale.setDefault(Locale.US);
     FastDateFormat fmt2 = FastDateFormat.getInstance();
     assertEquals(Locale.US, fmt2.getLocale());
     assertNotSame(fmt1, fmt2);
 }

 @Test
 public void testGetInstanceWithPatternDefaultLocaleChange() {
     String pattern = "yyyy-MM-dd";
     Locale.setDefault(Locale.GERMANY);
     FastDateFormat fmt1 = FastDateFormat.getInstance(pattern);
     assertEquals(Locale.GERMANY, fmt1.getLocale());

     Locale.setDefault(Locale.US);
     FastDateFormat fmt2 = FastDateFormat.getInstance(pattern);
     assertEquals(Locale.US, fmt2.getLocale());
     assertNotSame(fmt1, fmt2);
 }

 @Test
 public void testMultipleLocaleChanges() {
     Locale.setDefault(Locale.GERMANY);
     FastDateFormat fmt1 = FastDateFormat.getDateInstance(FastDateFormat.SHORT);
     assertEquals(Locale.GERMANY, fmt1.getLocale());

     Locale.setDefault(Locale.CANADA);
     FastDateFormat fmt2 = FastDateFormat.getDateInstance(FastDateFormat.SHORT);
     assertEquals(Locale.CANADA, fmt2.getLocale());

     Locale.setDefault(Locale.US);
     FastDateFormat fmt3 = FastDateFormat.getDateInstance(FastDateFormat.SHORT);
     assertEquals(Locale.US, fmt3.getLocale());

     assertNotSame(fmt1, fmt2);
     assertNotSame(fmt2, fmt3);
 }

 @Test
 public void testAllDateStylesDefaultLocaleChange() {
     int[] styles = {FastDateFormat.FULL, FastDateFormat.LONG, FastDateFormat.MEDIUM,
FastDateFormat.SHORT};
     for (int style : styles) {
         Locale.setDefault(Locale.GERMANY);
         FastDateFormat fmt1 = FastDateFormat.getDateInstance(style);
         assertEquals(Locale.GERMANY, fmt1.getLocale());

         Locale.setDefault(Locale.US);
         FastDateFormat fmt2 = FastDateFormat.getDateInstance(style);
         assertEquals(Locale.US, fmt2.getLocale());
         assertNotSame(fmt1, fmt2);
     }
 }

 @Test
 public void testAllTimeStylesDefaultLocaleChange() {
     int[] styles = {FastDateFormat.FULL, FastDateFormat.LONG, FastDateFormat.MEDIUM,
FastDateFormat.SHORT};
     for (int style : styles) {
         Locale.setDefault(Locale.GERMANY);
         FastDateFormat fmt1 = FastDateFormat.getTimeInstance(style);
         assertEquals(Locale.GERMANY, fmt1.getLocale());

         Locale.setDefault(Locale.US);
         FastDateFormat fmt2 = FastDateFormat.getTimeInstance(style);
         assertEquals(Locale.US, fmt2.getLocale());
         assertNotSame(fmt1, fmt2);
     }
 }

 @Test
 public void testAllDateTimeStylesDefaultLocaleChange() {
     int[] dateStyles = {FastDateFormat.FULL, FastDateFormat.MEDIUM};
     int[] timeStyles = {FastDateFormat.SHORT, FastDateFormat.MEDIUM};
     for (int ds : dateStyles) {
         for (int ts : timeStyles) {
             Locale.setDefault(Locale.GERMANY);
             FastDateFormat fmt1 = FastDateFormat.getDateTimeInstance(ds, ts);
             assertEquals(Locale.GERMANY, fmt1.getLocale());

             Locale.setDefault(Locale.US);
             FastDateFormat fmt2 = FastDateFormat.getDateTimeInstance(ds, ts);
             assertEquals(Locale.US, fmt2.getLocale());
             assertNotSame(fmt1, fmt2);
         }
     }
 }

}