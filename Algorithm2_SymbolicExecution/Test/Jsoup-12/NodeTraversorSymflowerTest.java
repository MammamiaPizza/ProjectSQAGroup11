package org.jsoup.select;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NodeTraversorSymflowerTest {
	@Test
	public void NodeTraversor1() {
		NodeVisitor visitor = mock(NodeVisitor.class);
		NodeTraversor expected = new NodeTraversor(null);
		NodeTraversor actual = new NodeTraversor(visitor);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}
}
