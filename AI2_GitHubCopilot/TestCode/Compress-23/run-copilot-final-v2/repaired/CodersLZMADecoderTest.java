package org.apache.commons.compress.archivers.sevenz;

 import static org.junit.Assert.*;

 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.lang.reflect.Field;
 import java.util.Arrays;

 import org.junit.Test;
 import org.tukaani.xz.LZMA2Options;
 import org.tukaani.xz.LZMAInputStream;
 import org.tukaani.xz.LZMAOutputStream;
 import org.tukaani.xz.UnsupportedOptionsException;

 /**

 - Tests for {@link Coders.LZMADecoder} focusing on dictionary-size
 - handling, including the bug reported in COMPRESS-256.
   */
  public class CodersLZMADecoderTest {
  private static final int MAX_DICT = LZMAInputStream.DICT_SIZE_MAX;
  // default lc=3, lp=0, pb=2  → (259 + 0*9 + 3) = 93 (0x5d)
  private static final int DEFAULT_PROPS = 0x5d;
  /** Build a little-endian properties array as expected by the decoder.
  */
  private static byte[] props(int propsByte, long dictSize) {
  byte[] p = new byte[5];
  p[0] = (byte) propsByte;
  p[1] = (byte) (dictSize       & 0xFF);
  p[2] = (byte) ((dictSize >> 8) & 0xFF);
  p[3] = (byte) ((dictSize >> 16) & 0xFF);
  p[4] = (byte) ((dictSize >> 24) & 0xFF);
  return p;
  }
  /** Create a {@link Coder} with the given properties (reflection for safety).
  */
  private Coder coderWithProps(byte[] properties) throws Exception {
  Coder coder = Coder.class.getDeclaredConstructor().newInstance();
  Field f = Coder.class.getDeclaredField("properties");
  f.setAccessible(true);
  f.set(coder, properties);
  return coder;
  }
  private final Coders.LZMADecoder decoder = new Coders.LZMADecoder();
  // ---------- Normal behaviour ----------
  @Test
  public void testDecodeWithDefaultDictionary() throws Exception {
  Coder coder = coderWithProps(props(DEFAULT_PROPS, 1L << 20));
  InputStream result = decoder.decode(new ByteArrayInputStream(new byte[0]), coder, null);
  assertNotNull(result);
  assertTrue("Expected LZMAInputStream", result instanceof LZMAInputStream);
  }
  @Test
  public void testDecodeWithMaxSupportedDictSize() throws Exception {
  // MAX_DICT is the library-side hard limit; should succeed (no UnsupportedOptionsException)
  Coder coder = coderWithProps(props(DEFAULT_PROPS, MAX_DICT));
  InputStream result = decoder.decode(new ByteArrayInputStream(new byte[0]), coder, null);
  assertNotNull(result);
  assertTrue("Expected LZMAInputStream", result instanceof LZMAInputStream);
  }
  @Test
  public void testDecodeRoundTrip() throws Exception {
  byte[] original = "Compress bug 23 round-trip".getBytes("UTF-8");
  int dictSize = 1 << 20;
  // encode
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  LZMA2Options opts = new LZMA2Options();
  opts.setDictSize(dictSize);
  try (LZMAOutputStream out = new LZMAOutputStream(baos, opts, false)) {
      out.write(original);
      out.finish();
  }
  // decode using the same parameters
  Coder coder = coderWithProps(props(DEFAULT_PROPS, dictSize));
  LZMAInputStream decoded = (LZMAInputStream) decoder.decode(
          new ByteArrayInputStream(baos.toByteArray()), coder, null);
  byte[] roundTrip = new byte[original.length];
  int n = 0;
  while (n < original.length) {
      int rd = decoded.read(roundTrip, n, original.length - n);
      if (rd < 0) break;
      n += rd;
  }
  assertArrayEquals("Round-trip data mismatch", original, Arrays.copyOf(roundTrip, n));
  assertEquals("Should have read everything", original.length, n);
  }
  // ---------- Oversized dictionary (correct rejection) ----------
  @Test(expected = IOException.class)
  public void testDecodeThrowsOnDictLargerThanMAX_DICT() throws Exception {
  long over = MAX_DICT + 1L;
  Coder coder = coderWithProps(props(DEFAULT_PROPS, over));
  decoder.decode(new ByteArrayInputStream(new byte[0]), coder, null);
  }
  // ---------- Bug reproduction: dict size within MAX_DICT but unsupported by LZMA ----------
  @Test(expected = UnsupportedOptionsException.class)
  public void testDecodeThrowsOnTooBigImplDict() throws Exception {
  // 1537 MiB exceeds LZMA implementation limit (~1536 MiB) but passes MAX_DICT check
  long tooBigForImpl = 1537L
  * 1024L * 1024L;
  Coder coder = coderWithProps(props(DEFAULT_PROPS, tooBigForImpl));
  decoder.decode(new ByteArrayInputStream(new byte[0]), coder, null);
  }
  // ---------- Boundary & invalid properties ----------
  @Test(expected = UnsupportedOptionsException.class)
  public void testDecodeWithZeroDictSize() throws Exception {
  Coder coder = coderWithProps(props(DEFAULT_PROPS, 0L));
  decoder.decode(new ByteArrayInputStream(new byte[0]), coder, null);
  }
  @Test
  public void testDecodeWithNegativeLikeDictBytes() throws Exception {
  // All 0xFF bytes → dictSize becomes 0xFFFF_FFFF (signed negative int after cast)
  // The cast to (int)dictSize gives -1; constructor should throw
  // UnsupportedOptionsException.
  byte[] p = new byte[5];
  Arrays.fill(p, (byte) 0xFF);
  p[0] = (byte) DEFAULT_PROPS; // keep a valid props byte
  Coder coder = coderWithProps(p);
  try {
      decoder.decode(new ByteArrayInputStream(new byte[0]), coder, null);
      fail("Should have thrown an exception for negative-like dict size");
  } catch (UnsupportedOptionsException | IllegalArgumentException e) {
      // expected
  }
  }
  @Test(expected = UnsupportedOptionsException.class)
  public void testDecodeWithInvalidPropsByte() throws Exception {
  // lc=8, lp=4, pb=4 gives 859 + 4*9 + 4 = 0, which is valid?
  // A clearly out-of-range value: 0xFF (lc=9+? invalid)
  Coder coder = coderWithProps(props(0xFF, 1L << 20));
  decoder.decode(new ByteArrayInputStream(new byte[0]), coder, null);
  }
  @Test(expected = NullPointerException.class)
  public void testDecodeWithNullProperties() throws Exception {
  Coder coder = coderWithProps(null);
  decoder.decode(new ByteArrayInputStream(new byte[0]), coder, null);
  }
  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void testDecodeWithTooShortProperties() throws Exception {
  // length 3 – cannot read properties[4]
  Coder coder = new Coder();
  Field f = Coder.class.getDeclaredField("properties");
  f.setAccessible(true);
  f.set(coder, new byte[3]);
  decoder.decode(new ByteArrayInputStream(new byte[0]), coder, null);
  }
  // ---------- Defensive: ensure password is ignored by LZMA ----------
  @Test
  public void testDecodeWithNonNullPassword() throws Exception {
  // LZMADecoder ignores the password argument; must not throw.
  Coder coder = coderWithProps(props(DEFAULT_PROPS, 1L << 20));
  InputStream in = decoder.decode(new ByteArrayInputStream(new byte[0]), coder,
          "secret".getBytes("UTF-8"));
  assertNotNull(in);
  assertTrue(in instanceof LZMAInputStream);
  }

 }
