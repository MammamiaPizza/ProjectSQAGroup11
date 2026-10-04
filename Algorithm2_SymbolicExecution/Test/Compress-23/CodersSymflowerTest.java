package org.apache.commons.compress.archivers.sevenz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.junit.*;
import static org.junit.Assert.*;

public class CodersSymflowerTest {
	@Test
	public void copyDecoderDecode1() throws IOException {
		Coders.CopyDecoder c = new Coders.CopyDecoder();
		InputStream in = null;
		Coder coder = null;
		byte[] password = null;
		InputStream actual = c.decode(in, coder, password);

		assertNull(actual);
	}

	@Test
	public void copyDecoderEncode2() {
		Coders.CopyDecoder c = new Coders.CopyDecoder();
		OutputStream out = null;
		byte[] password = null;
		OutputStream actual = c.encode(out, password);

		assertNull(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void lzmaDecoderDecode3() throws IOException {
		Coders.LZMADecoder l = new Coders.LZMADecoder();
		InputStream in = null;
		Coder coder = null;
		byte[] password = null;
		l.decode(in, coder, password);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void lzmaDecoderDecode4() throws IOException {
		Coders.LZMADecoder l = new Coders.LZMADecoder();
		InputStream in = null;
		Coder coder = new Coder();
		byte[] password = null;
		l.decode(in, coder, password);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void lzmaDecoderDecode5() throws IOException {
		Coders.LZMADecoder l = new Coders.LZMADecoder();
		InputStream in = null;
		Coder coder = new Coder();
		coder.properties = new byte[]{};
		byte[] password = null;
		l.decode(in, coder, password);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void lzmaDecoderDecode6() throws IOException {
		Coders.LZMADecoder l = new Coders.LZMADecoder();
		InputStream in = null;
		Coder coder = new Coder();
		coder.properties = new byte[]{ 0 };
		byte[] password = null;
		l.decode(in, coder, password);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void lzmaDecoderDecode7() throws IOException {
		Coders.LZMADecoder l = new Coders.LZMADecoder();
		InputStream in = null;
		Coder coder = new Coder();
		coder.properties = new byte[]{ 0, 0 };
		byte[] password = null;
		l.decode(in, coder, password);
	}
}
