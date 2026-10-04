package org.apache.commons.compress.compressors.xz;

import java.io.IOException;
import org.junit.*;

public class XZCompressorInputStreamSymflowerTest {
	@Test // (expected = java.lang.NullPointerException.class)
	public void available70() throws IOException {
		XZCompressorInputStream x = new XZCompressorInputStream(null);
		x.available();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void close71() throws IOException {
		XZCompressorInputStream x = new XZCompressorInputStream(null);
		x.close();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void read72() throws IOException {
		XZCompressorInputStream x = new XZCompressorInputStream(null);
		x.read();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void read73() throws IOException {
		XZCompressorInputStream x = new XZCompressorInputStream(null);
		byte[] buf = null;
		int off = 0;
		int len = 0;
		x.read(buf, off, len);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void skip74() throws IOException {
		XZCompressorInputStream x = new XZCompressorInputStream(null);
		long n = 0L;
		x.skip(n);
	}
}
