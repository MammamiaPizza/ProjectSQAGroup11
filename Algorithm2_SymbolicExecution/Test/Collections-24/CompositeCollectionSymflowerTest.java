package org.apache.commons.collections4.collection;

import java.util.Collection;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class CompositeCollectionSymflowerTest {
	@Test
	public void getMutator1() {
		CompositeCollection<Object> o = new CompositeCollection<Object>((Collection<Object>[]) null);
		CompositeCollection.CollectionMutator<Object> actual = o.getMutator();

		assertNull(actual);
	}

	@Test
	public void setMutator2() {
		CompositeCollection<Object> o = new CompositeCollection<Object>((Collection<Object>[]) null);
		CompositeCollection.CollectionMutator<Object> mutator = null;
		o.setMutator(mutator);

		CompositeCollection<Object> oExpected = new CompositeCollection<Object>((Collection<Object>[]) null);

		assertTrue(EqualsBuilder.reflectionEquals(oExpected, o, false, null, true));
	}
}
