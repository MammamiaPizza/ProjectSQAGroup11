package org.apache.commons.codec.language;

import org.apache.commons.codec.EncoderException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DoubleMetaphoneBug3Test {

 private final DoubleMetaphone dm = new DoubleMetaphone();

 @Test
 public void testAlternateForAngierIsANJR() {
     assertEquals("ANJR", dm.doubleMetaphone("Angier", true));
 }

 @Test
 public void testPrimaryForAngierIsANJR() {
     assertEquals("ANJR", dm.doubleMetaphone("Angier", false));
 }

 @Test
 public void testDefaultEncodingUsesPrimary() {
     assertEquals("ANJR", dm.doubleMetaphone("Angier"));
 }

 @Test
 public void testNullInputReturnsNull() {
     assertNull(dm.doubleMetaphone(null));
     assertNull(dm.doubleMetaphone(null, true));
 }

 @Test
 public void testDefaultMaxCodeLenIsFour() {
     assertEquals(4, dm.getMaxCodeLen());
 }

 @Test
 public void testMaxCodeLenZeroYieldsEmptyResult() {
     DoubleMetaphone bounded = new DoubleMetaphone();
     bounded.setMaxCodeLen(0);
     assertEquals("", bounded.doubleMetaphone("Angier"));
     assertEquals("", bounded.doubleMetaphone("Angier", true));
 }

 @Test
 public void testSetMaxCodeLenRoundTrip() {
     DoubleMetaphone bounded = new DoubleMetaphone();
     bounded.setMaxCodeLen(3);
     assertEquals(3, bounded.getMaxCodeLen());
     assertEquals("ANJ", bounded.doubleMetaphone("Angier"));
 }

 @Test
 public void testEncodeStringMatchesDoubleMetaphone() {
     assertEquals(dm.doubleMetaphone("Angier"), dm.encode("Angier"));
 }

 @Test
 public void testEncodeObjectStringUsesPrimaryEncoding() throws EncoderException {
     Object result = dm.encode((Object) "Angier");
     assertEquals("ANJR", result);
 }

 @Test
 public void testEncodeObjectNonStringThrows() throws EncoderException {
     try {
         dm.encode(Integer.valueOf(42));
         fail("Expected EncoderException for non-String input");
     } catch (EncoderException expected) {
         // expected
     }
 }

 @Test
 public void testIsDoubleMetaphoneEqualPrimary() {
     assertTrue(dm.isDoubleMetaphoneEqual("Angier", "Angier"));
 }

 @Test
 public void testIsDoubleMetaphoneEqualAlternate() {
     assertTrue(dm.isDoubleMetaphoneEqual("Angier", "Angier", true));
 }

}
