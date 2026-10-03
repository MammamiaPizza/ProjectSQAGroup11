import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MappingIteratorRecoveryTest
{
    private final ObjectMapper mapper = new ObjectMapper();

    public static class Bean {
        public int id;

        public Bean() { }
    }

    @Test
    public void recoversAfterInvalidRootValueUsingCheckedMethods() throws Exception {
        MappingIterator<Bean> iterator = mapper.reader(Bean.class)
                .readValues("1 {\"id\":2} {\"id\":3}");

        assertTrue(iterator.hasNextValue());
        try {
            iterator.nextValue();
            fail("Expected invalid scalar root value to fail as a Bean");
        } catch (JsonMappingException e) {
            // expected
        }

        assertTrue(iterator.hasNextValue());
        assertEquals(2, iterator.nextValue().id);
        assertTrue(iterator.hasNextValue());
        assertEquals(3, iterator.nextValue().id);
        assertFalse(iterator.hasNextValue());
    }

    @Test
    public void recoversAfterInvalidArrayElement() throws Exception {
        MappingIterator<Bean> iterator = mapper.reader(Bean.class)
                .readValues("[1,{\"id\":2},{\"id\":3}]");

        try {
            iterator.nextValue();
            fail("Expected invalid scalar array element to fail as a Bean");
        } catch (JsonMappingException e) {
            // expected
        }

        assertTrue(iterator.hasNextValue());
        assertEquals(2, iterator.nextValue().id);
        assertTrue(iterator.hasNextValue());
        assertEquals(3, iterator.nextValue().id);
        assertFalse(iterator.hasNextValue());
    }

    @Test
    public void nextWrapsMappingFailureAndIteratorStillRecovers() throws Exception {
        MappingIterator<Bean> iterator = mapper.reader(Bean.class)
                .readValues("1 {\"id\":7}");

        try {
            iterator.next();
            fail("Iterator next() must wrap mapping failures");
        } catch (RuntimeJsonMappingException e) {
            assertTrue(e.getCause() instanceof JsonMappingException);
        }

        assertTrue(iterator.hasNext());
        assertEquals(7, iterator.next().id);
        assertFalse(iterator.hasNext());
    }

    @Test
    public void readAllReadsValidRootSequenceInOrderIntoProvidedList() throws Exception {
        MappingIterator<Bean> iterator = mapper.reader(Bean.class)
                .readValues("{\"id\":4} {\"id\":5} {\"id\":6}");
        List<Bean> result = new ArrayList<Bean>();

        List<Bean> returned = iterator.readAll(result);

        assertSame(result, returned);
        assertEquals(3, result.size());
        assertEquals(4, result.get(0).id);
        assertEquals(5, result.get(1).id);
        assertEquals(6, result.get(2).id);
        assertFalse(iterator.hasNextValue());
    }

@Test
public void readAllWithoutTargetCollectsRootValues() throws Exception {
    com.fasterxml.jackson.databind.MappingIterator<java.lang.Integer> iterator =
            new com.fasterxml.jackson.databind.ObjectMapper().reader(java.lang.Integer.class)
                    .readValues("1 2 3");

    java.util.List<java.lang.Integer> values = iterator.readAll();

    assertEquals(java.util.Arrays.asList(
            java.lang.Integer.valueOf(1),
            java.lang.Integer.valueOf(2),
            java.lang.Integer.valueOf(3)), values);
    assertFalse(iterator.hasNextValue());
}

@Test
public void readAllAddsValuesToGenericCollection() throws Exception {
    com.fasterxml.jackson.databind.MappingIterator<java.lang.Integer> iterator =
            new com.fasterxml.jackson.databind.ObjectMapper().reader(java.lang.Integer.class)
                    .readValues("1 2 2 3");
    java.util.Collection<java.lang.Integer> values =
            new java.util.LinkedHashSet<java.lang.Integer>();

    iterator.readAll(values);

    assertEquals(java.util.Arrays.asList(
            java.lang.Integer.valueOf(1),
            java.lang.Integer.valueOf(2),
            java.lang.Integer.valueOf(3)),
            new java.util.ArrayList<java.lang.Integer>(values));
}

@Test
public void closedIteratorHasNoValuesAndNextFails() throws Exception {
    com.fasterxml.jackson.databind.MappingIterator<java.lang.Integer> iterator =
            new com.fasterxml.jackson.databind.ObjectMapper().reader(java.lang.Integer.class)
                    .readValues("1");

    iterator.close();

    assertFalse(iterator.hasNextValue());
    assertFalse(iterator.hasNext());
    try {
        iterator.next();
        fail("Expected next on a closed iterator to fail");
    } catch (java.util.NoSuchElementException e) {
        // expected
    }
}

@Test
public void removeIsUnsupported() throws Exception {
    com.fasterxml.jackson.databind.MappingIterator<java.lang.Integer> iterator =
            new com.fasterxml.jackson.databind.ObjectMapper().reader(java.lang.Integer.class)
                    .readValues("1");

    try {
        iterator.remove();
        fail("Expected remove to be unsupported");
    } catch (java.lang.UnsupportedOperationException e) {
        // expected
    }
}
}
