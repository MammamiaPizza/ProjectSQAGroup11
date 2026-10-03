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
}
