package org.apache.commons.codec.net;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class BCodecSymflowerTest {
	@Test
	public void BCodec1() {
		String charset = null;
		BCodec expected = new BCodec(null);
		BCodec actual = new BCodec(charset);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void decode2() throws DecoderException {
		BCodec b = new BCodec(null);
		Object value = null;
		Object actual = b.decode(value);

		assertNull(actual);
	}

	@Test
	public void decode3() throws DecoderException {
		BCodec b = new BCodec(null);
		String value = null;
		String actual = b.decode(value);

		assertNull(actual);
	}

	@Test
	public void doDecoding4() {
		BCodec b = new BCodec(null);
		byte[] bytes = null;
		byte[] actual = b.doDecoding(bytes);

		assertNull(actual);
	}

	@Test
	public void doEncoding5() {
		BCodec b = new BCodec(null);
		byte[] bytes = null;
		byte[] actual = b.doEncoding(bytes);

		assertNull(actual);
	}

	@Test
	public void encode6() throws EncoderException {
		BCodec b = new BCodec(null);
		Object value = null;
		Object actual = b.encode(value);

		assertNull(actual);
	}

	@Test
	public void encode7() throws EncoderException {
		BCodec b = new BCodec(null);
		String value = null;
		String charset = null;
		String actual = b.encode(value, charset);

		assertNull(actual);
	}

	@Test
	public void encode8() throws EncoderException {
		BCodec b = new BCodec(null);
		String value = null;
		String actual = b.encode(value);

		assertNull(actual);
	}

	@Test
	public void getDefaultCharset9() {
		BCodec b = new BCodec(null);
		String actual = b.getDefaultCharset();

		assertNull(actual);
	}

	@Test
	public void getEncoding10() {
		BCodec b = new BCodec(null);
		String expected = "B";
		String actual = b.getEncoding();

		assertEquals(expected, actual);
	}
}
