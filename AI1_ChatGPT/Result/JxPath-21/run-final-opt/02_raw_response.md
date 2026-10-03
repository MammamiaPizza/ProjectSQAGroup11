import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

public class PropertyPointerTest extends TestCase {

    public void testDistinctMapPropertiesAreNotEqualInXPathComparison() {
        Map values = new HashMap();
        values.put("a", "A");
        values.put("b", "B");

        JXPathContext context = JXPathContext.newContext(new MapHolder(values));

        assertEquals(Boolean.TRUE, context.getValue("map/b != map/a"));
    }

    public void testPointersForSameMapPropertyAreEqualAndHaveSameHashCode() {
        Map values = new HashMap();
        values.put("a", "value");

        JXPathContext context = JXPathContext.newContext(new MapHolder(values));
        Pointer first = context.getPointer("map/a");
        Pointer second = context.getPointer("map/a");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    public void testNullPropertySelectedByIndexProducesOneNullValue() {
        JXPathContext context = JXPathContext.newContext(new Object());
        context.setVariable("testnull", new NullValueBean());

        Iterator values = context.iterate("$testnull/nothing[1]");

        assertTrue(values.hasNext());
        assertNull(values.next());
        assertFalse(values.hasNext());
    }

    public void testNonNullPropertySelectedByIndexProducesItsValue() {
        JXPathContext context = JXPathContext.newContext(new Object());
        context.setVariable("bean", new StringValueBean("present"));

        Iterator values = context.iterate("$bean/value[1]");

        assertTrue(values.hasNext());
        assertEquals("present", values.next());
        assertFalse(values.hasNext());
    }

    public static class MapHolder {
        private final Map map;

        public MapHolder(Map map) {
            this.map = map;
        }

        public Map getMap() {
            return map;
        }
    }

    public static class NullValueBean {
        public Object getNothing() {
            return null;
        }
    }

    public static class StringValueBean {
        private final String value;

        public StringValueBean(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}