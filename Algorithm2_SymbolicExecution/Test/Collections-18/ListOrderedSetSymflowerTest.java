package org.apache.commons.collections.set;

import java.util.List;
import org.junit.*;

public class ListOrderedSetSymflowerTest {
	@Test(expected = IllegalArgumentException.class)
	public void listOrderedSet1() throws IllegalArgumentException {
		List<Object> list = null;
		ListOrderedSet.listOrderedSet(list);
	}
}
