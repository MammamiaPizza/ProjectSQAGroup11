package org.apache.commons.compress.archivers.zip;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.*;
import static org.junit.Assert.*;

public class ZipArchiveOutputStreamSymflowerTest {
	@Test
	public void unicodeExtraFieldPolicyToString1() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Constructor c = ZipArchiveOutputStream.UnicodeExtraFieldPolicy.class.getDeclaredConstructor(String.class);
		c.setAccessible(true);
		ZipArchiveOutputStream.UnicodeExtraFieldPolicy u = (ZipArchiveOutputStream.UnicodeExtraFieldPolicy) c.newInstance((Object) null);
		String actual = u.toString();

		assertNull(actual);
	}
}
