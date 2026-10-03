package org.apache.commons.lang3.text;

 import static org.junit.Assert.*;
 import org.junit.Test;

 import java.text.Format;
 import java.text.MessageFormat;
 import java.util.HashMap;
 import java.util.LinkedHashMap;
 import java.util.Locale;
 import java.util.Map;
 import java.util.TreeMap;

 public class ExtendedMessageFormatTest {

     private static final class TestFormatFactory implements FormatFactory {
         private final String id;
         TestFormatFactory(String id) { this.id = id; }
         @Override public Format getFormat(String name, String args, Locale locale) { return null; }
         @Override public boolean equals(Object o) {
             if (this == o) return true;
             if (!(o instanceof TestFormatFactory)) return false;
             return id.equals(((TestFormatFactory)o).id);
         }
         @Override public int hashCode() { return id.hashCode(); }
     }

     private static final String PATTERN = "Hello {0,choice,0#zero|1#one}";
     private static final Locale LOCALE = Locale.US;

     @Test
     public void testEqualsSamePatternLocaleRegistration() {
         Map<String, FormatFactory> reg1 = new HashMap<String, FormatFactory>();
         reg1.put("choice", new TestFormatFactory("choice"));
         reg1.put("number", new TestFormatFactory("number"));
         Map<String, FormatFactory> reg2 = new HashMap<String, FormatFactory>();
         reg2.put("choice", new TestFormatFactory("choice"));
         reg2.put("number", new TestFormatFactory("number"));
         ExtendedMessageFormat fmt1 = new ExtendedMessageFormat(PATTERN, LOCALE, reg1);
         ExtendedMessageFormat fmt2 = new ExtendedMessageFormat(PATTERN, LOCALE, reg2);
         assertEquals("should be equal with same registry content", fmt1, fmt2);
         assertEquals("hashcodes should match", fmt1.hashCode(), fmt2.hashCode());
     }

     @Test
     public void testEqualsSameRegistryDifferentMapImpplementation() {
         Map<String, FormatFactory> regHashMap = new HashMap<String, FormatFactory>();
         regHashMap.put("choice", new TestFormatFactory("choice"));
         regHashMap.put("number", new TestFormatFactory("number"));
         Map<String, FormatFactory> regLinked = new LinkedHashMap<String, FormatFactory>();
         regLinked.put("choice", new TestFormatFactory("choice"));
         regLinked.put("number", new TestFormatFactory("number"));
         ExtendedMessageFormat fmt1 = new ExtendedMessageFormat(PATTERN, LOCALE, regHashMap);
         ExtendedMessageFormat fmt2 = new ExtendedMessageFormat(PATTERN, LOCALE, regLinked);
         assertEquals("should be equal regardless of Map implementation", fmt1, fmt2);
         assertEquals("hashcodes should match", fmt1.hashCode(), fmt2.hashCode());
     }

     @Test
     public void testEqualsWithNullRegistrySamePatternLocale() {
         ExtendedMessageFormat fmt1 = new ExtendedMessageFormat(PATTERN, LOCALE);
         ExtendedMessageFormat fmt2 = new ExtendedMessageFormat(PATTERN, LOCALE);
         assertEquals("should be equal when both have null registry", fmt1, fmt2);
         assertEquals("hashcodes should match", fmt1.hashCode(), fmt2.hashCode());
     }

     @Test
     public void testEqualsNullRegistryVsNonNullRegistry() {
         ExtendedMessageFormat fmtNullReg = new ExtendedMessageFormat(PATTERN, LOCALE);
         Map<String, FormatFactory> reg = new HashMap<String, FormatFactory>();
         reg.put("choice", new TestFormatFactory("choice"));
         ExtendedMessageFormat fmtReg = new ExtendedMessageFormat(PATTERN, LOCALE, reg);
         assertFalse("should not be equal when one has null registry", fmtNullReg.equals(fmtReg));
         assertFalse("should not be equal symmetry", fmtReg.equals(fmtNullReg));
     }

     @Test
     public void testEqualsDifferentRegistryContent() {
         Map<String, FormatFactory> reg1 = new HashMap<String, FormatFactory>();
         reg1.put("choice", new TestFormatFactory("choice"));
         Map<String, FormatFactory> reg2 = new HashMap<String, FormatFactory>();
         reg2.put("choice", new TestFormatFactory("differentChoiceFactory"));
         ExtendedMessageFormat fmt1 = new ExtendedMessageFormat(PATTERN, LOCALE, reg1);
         ExtendedMessageFormat fmt2 = new ExtendedMessageFormat(PATTERN, LOCALE, reg2);
         assertFalse("should not be equal with different registry factories", fmt1.equals(fmt2));
     }

     @Test
     public void testEqualsDifferentPattern() {
         ExtendedMessageFormat fmt1 = new ExtendedMessageFormat("pattern1", LOCALE);
         ExtendedMessageFormat fmt2 = new ExtendedMessageFormat("pattern2", LOCALE);
         assertFalse("should not be equal with different patterns", fmt1.equals(fmt2));
     }

     @Test
     public void testEqualsDifferentLocale() {
         ExtendedMessageFormat fmt1 = new ExtendedMessageFormat(PATTERN, Locale.US);
         ExtendedMessageFormat fmt2 = new ExtendedMessageFormat(PATTERN, Locale.UK);
         assertFalse("should not be equal with different locales", fmt1.equals(fmt2));
     }

     @Test
     public void testEqualsWithDifferentLocale() {
         ExtendedMessageFormat fmtUS = new ExtendedMessageFormat(PATTERN, Locale.US);
         ExtendedMessageFormat fmtUK = new ExtendedMessageFormat(PATTERN, Locale.UK);
         assertFalse("should not be equal with different locales", fmtUS.equals(fmtUK));
     }

     @Test
     public void testEqualsReflexive() {
         Map<String, FormatFactory> reg = new HashMap<String, FormatFactory>();
         reg.put("choice", new TestFormatFactory("choice"));
         ExtendedMessageFormat fmt = new ExtendedMessageFormat(PATTERN, LOCALE, reg);
         assertTrue("should be reflexive", fmt.equals(fmt));
     }

     @Test
     public void testEqualsSymmetric() {
         Map<String, FormatFactory> reg = new HashMap<String, FormatFactory>();
         reg.put("choice", new TestFormatFactory("choice"));
         ExtendedMessageFormat fmt1 = new ExtendedMessageFormat(PATTERN, LOCALE, reg);
         ExtendedMessageFormat fmt2 = new ExtendedMessageFormat(PATTERN, LOCALE, reg);
         assertTrue("should be symmetric", fmt1.equals(fmt2) && fmt2.equals(fmt1));
     }

     @Test
     public void testEqualsNull() {
         ExtendedMessageFormat fmt = new ExtendedMessageFormat(PATTERN, LOCALE);
         assertFalse("should not be equal to null", fmt.equals(null));
     }

     @Test
     public void testEqualsDifferentClass() {
         ExtendedMessageFormat fmt = new ExtendedMessageFormat(PATTERN, LOCALE);
         assertFalse("should not be equal to a non-ExtendedMessageFormat object", fmt.equals(new
Object()));
     }
 }