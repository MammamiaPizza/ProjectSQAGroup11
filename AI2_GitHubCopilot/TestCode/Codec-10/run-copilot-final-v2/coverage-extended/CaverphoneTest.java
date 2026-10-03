package org.apache.commons.codec.language;

import org.apache.commons.codec.EncoderException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CaverphoneTest {

 private final Caverphone encoder = new Caverphone();

 @Test
 public void testEndMb() {
     assertEquals("MPM1111111", encoder.caverphone("mbmb"));
 }

 @Test
 public void testEncodeStringUsesCaverphone() {
     assertEquals("MPM1111111", encoder.encode("mbmb"));
 }

 @Test
 public void testEncodeObjectString() throws EncoderException {
     assertEquals("MPM1111111", encoder.encode((Object) "mbmb"));
 }

 @Test(expected = EncoderException.class)
 public void testEncodeObjectNonStringThrowsEncoderException() throws EncoderException {
     encoder.encode(Integer.valueOf(1));
 }

 @Test
 public void testNullReturnsTenOnes() {
     assertEquals("1111111111", encoder.caverphone(null));
 }

 @Test
 public void testEmptyStringReturnsTenOnes() {
     assertEquals("1111111111", encoder.caverphone(""));
 }

 @Test
 public void testIsCaverphoneEqualIsCaseInsensitive() {
     assertTrue(encoder.isCaverphoneEqual("mbmb", "MBMB"));
 }

 @Test
 public void testIsCaverphoneEqualFalseWhenComparedToNull() {
     assertFalse(encoder.isCaverphoneEqual("mbmb", null));
 }

}
