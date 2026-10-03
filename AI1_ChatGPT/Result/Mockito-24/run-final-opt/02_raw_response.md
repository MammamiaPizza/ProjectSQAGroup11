package org.mockito.internal.stubbing.defaultanswers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;

public class ReturnsEmptyValuesBug467Test {

    @Test
    public void shouldReturnZeroWhenComparableMockIsComparedToItself() {
        Comparable comparable = mock(Comparable.class);

        assertEquals(0, comparable.compareTo(comparable));
    }
}