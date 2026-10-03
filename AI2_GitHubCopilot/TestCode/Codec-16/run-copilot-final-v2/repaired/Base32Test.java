package org.apache.commons.codec.binary;

 import static org.junit.Assert.assertArrayEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;

 import java.io.UnsupportedEncodingException;
 import java.util.Arrays;
 import java.util.Random;

 import org.junit.Test;

 public class Base32Test {

     private static final byte[] EMPTY = new byte[0];
     private static final byte[] HEX_DATA = new byte[] {0, 1, 2, 3, 4, 5, (byte) 128, (byte) 255};
     private static final String TEXT = "Hello World! This is a test string for Base32 encoding.";
     private static final byte[] BYTES_A = new byte[] {65, 66, 67, 68, 69};
     private static final byte[] BYTES_ODD = new byte[] {1, 2, 3, 4, 5};

     private byte[] toBytes(String s) {
         try {
             return s.getBytes("UTF-8");
         } catch (UnsupportedEncodingException e) {
             throw new RuntimeException(e);
         }
     }

     // Round-trip helper
     private void assertRoundTrip(Base32 encoder, byte[] original) {
         byte[] encoded = encoder.encode(original);
         byte[] decoded = encoder.decode(encoded);
         assertArrayEquals("Round-trip failed for encoder with pad=" + (char)encoder.pad, original,
decoded);
     }

     // ---------- Tests ----------

     @Test
     public void testDefaultPad() {
         Base32 b32 = new Base32();
         assertRoundTrip(b32, BYTES_A);
         assertRoundTrip(b32, BYTES_ODD);
         assertRoundTrip(b32, toBytes(TEXT));
         assertRoundTrip(b32, HEX_DATA);
     }

     @Test
     public void testNonDefaultValidPads() {
         byte[] pads = new byte[] {'*', '~', '@', '!', 0x00, (byte) 0xFF};
         byte[] data = toBytes(TEXT);
         for (byte pad : pads) {
             Base32 b32 = new Base32(pad);
             assertRoundTrip(b32, data);
         }
     }

     @Test(expected = IllegalArgumentException.class)
     public void testPadAlphabet_A() {
         new Base32((byte) 'A');
     }

     @Test(expected = IllegalArgumentException.class)
     public void testPadAlphabet_Z() {
         new Base32((byte) 'Z');
     }

     @Test(expected = IllegalArgumentException.class)
     public void testPadAlphabet_2() {
         new Base32((byte) '2');
     }

     @Test(expected = IllegalArgumentException.class)
     public void testPadAlphabet_7() {
         new Base32((byte) '7');
     }

     @Test(expected = IllegalArgumentException.class)
     public void testPadWhitespace_Space() {
         new Base32((byte) ' ');
     }

     @Test(expected = IllegalArgumentException.class)
     public void testPadWhitespace_Tab() {
         new Base32((byte) '\t');
     }

     @Test(expected = IllegalArgumentException.class)
     public void testPadWhitespace_Newline() {
         new Base32((byte) '\n');
     }

     @Test
     public void testCodec200_Reproduce() {
         // The bug: using hex=true with pad '=' should succeed (default pad is not in hex
alphabet).
         // With hex=true, alphabet is 0-9 A-V, so '=' is still valid pad.
         Base32 b32 = new Base32(true, (byte) '=');
         byte[] data = toBytes("CODEC-200 regression test");
         assertRoundTrip(b32, data);

         // Another case from the same bug: hex=false, pad='=' already worked; hex=true and any
non-alphabet pad should work.
         Base32 b32hex = new Base32(true);
         assertRoundTrip(b32hex, data);
     }

     @Test
     public void testIsInAlphabet() {
         Base32 b32 = new Base32();
         assertTrue(b32.isInAlphabet((byte) 'A'));
         assertTrue(b32.isInAlphabet((byte) 'Z'));
         assertTrue(b32.isInAlphabet((byte) '2'));
         assertTrue(b32.isInAlphabet((byte) '7'));
         assertFalse(b32.isInAlphabet((byte) '='));
         assertFalse(b32.isInAlphabet((byte) '1'));
         assertFalse(b32.isInAlphabet((byte) '8'));
         assertFalse(b32.isInAlphabet((byte) '*'));
         assertFalse(b32.isInAlphabet((byte) ' '));
         assertFalse(b32.isInAlphabet((byte) -1));
     }

     @Test
     public void testIsInAlphabetHex() {
         Base32 b32 = new Base32(true);
         assertTrue(b32.isInAlphabet((byte) '0'));
         assertTrue(b32.isInAlphabet((byte) '9'));
         assertTrue(b32.isInAlphabet((byte) 'A'));
         assertTrue(b32.isInAlphabet((byte) 'V'));
         assertFalse(b32.isInAlphabet((byte) 'W'));
         assertFalse(b32.isInAlphabet((byte) 'Z'));
         assertFalse(b32.isInAlphabet((byte) '='));
     }
 }
