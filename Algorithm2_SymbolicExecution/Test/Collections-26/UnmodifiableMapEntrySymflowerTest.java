package org.apache.commons.collections4.keyvalue;

import java.util.Map;
import org.junit.*;

public class UnmodifiableMapEntrySymflowerTest {
	@Test // (expected = java.lang.UnsupportedOperationException.class)
	public void setValue13() throws UnsupportedOperationException {
		UnmodifiableMapEntry<Object, Object> o = new UnmodifiableMapEntry<Object, Object>((Map.Entry<?, ?>) null);
		Object value = null;
		o.setValue(value);
	}
}
