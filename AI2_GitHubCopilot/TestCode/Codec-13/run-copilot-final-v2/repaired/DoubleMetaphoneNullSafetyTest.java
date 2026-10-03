package org.apache.commons.codec.language;

 import static org.junit.Assert.*;

 import org.apache.commons.codec.binary.StringUtils;
 import org.junit.Test;

 public class DoubleMetaphoneNullSafetyTest {

     @Test
     public void testDoubleMetaphoneNull() {
         DoubleMetaphone dm = new DoubleMetaphone();
         assertNull(dm.doubleMetaphone(null));
     }

     @Test
     public void testDoubleMetaphoneNullAlternate() {
         DoubleMetaphone dm = new DoubleMetaphone();
         assertNull(dm.doubleMetaphone(null, true));
     }

     @Test
     public void testEncodeNull() {
         DoubleMetaphone dm = new DoubleMetaphone();
         assertNull(dm.encode((String) null));
     }

     @Test(expected = NullPointerException.class)
     public void testIsDoubleMetaphoneEqualBothNull() {
         DoubleMetaphone dm = new DoubleMetaphone();
         dm.isDoubleMetaphoneEqual(null, null);
     }

     @Test
     public void testIsDoubleMetaphoneEqualFirstNull() {
         DoubleMetaphone dm = new DoubleMetaphone();
         assertFalse(dm.isDoubleMetaphoneEqual(null, "abc"));
     }

     @Test
     public void testIsDoubleMetaphoneEqualSecondNull() {
         DoubleMetaphone dm = new DoubleMetaphone();
         assertFalse(dm.isDoubleMetaphoneEqual("abc", null));
     }

     @Test
     public void testStringUtilsGetBytesNull() {
         assertNull(StringUtils.getBytesIso8859_1(null));
         assertNull(StringUtils.getBytesUsAscii(null));
         assertNull(StringUtils.getBytesUtf8(null));
         assertNull(StringUtils.getBytesUtf16(null));
         assertNull(StringUtils.getBytesUtf16Be(null));
         assertNull(StringUtils.getBytesUtf16Le(null));
         assertNull(StringUtils.getBytesUnchecked(null, "UTF-8"));
     }

     @Test
     public void testIsDoubleMetaphoneEqualEmptyStrings() {
         DoubleMetaphone dm = new DoubleMetaphone();
         assertTrue(dm.isDoubleMetaphoneEqual("", ""));
     }

     @Test
     public void testIsDoubleMetaphoneEqualSameString() {
         DoubleMetaphone dm = new DoubleMetaphone();
         assertTrue(dm.isDoubleMetaphoneEqual("Smith", "Smith"));
     }

     @Test
     public void testIsDoubleMetaphoneEqualDifferentStrings() {
         DoubleMetaphone dm = new DoubleMetaphone();
         assertFalse(dm.isDoubleMetaphoneEqual("Smith", "Jones"));
     }

     @Test
     public void testDoubleMetaphoneBasicOutput() {
         DoubleMetaphone dm = new DoubleMetaphone();
         assertNotNull(dm.doubleMetaphone("hello"));
     }

     @Test
     public void testDoubleMetaphoneMaxCodeLenTruncation() {
         DoubleMetaphone dm = new DoubleMetaphone();
         dm.setMaxCodeLen(2);
         String result = dm.doubleMetaphone("Catherine");
         assertNotNull(result);
         assertTrue(result.length() <= 2);
     }

 }
