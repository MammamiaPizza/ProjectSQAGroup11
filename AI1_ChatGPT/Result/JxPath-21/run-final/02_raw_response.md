```java
package org.apache.commons.jxpath.ri.model.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

/**
 * Tests for PropertyPointer behavior through the public JXPath API.
 */
public class PropertyPointerTest extends TestCase {

    public void testMapPropertiesWithDifferentNamesAreDifferentPointersAndValues() {
        MapHolder holder = new MapHolder();
        holder.getMap().put("a", "a");
        holder.getMap().put("b", "b");

        JXPathContext context = JXPathContext.newContext(holder);

        Pointer pointerA = context.getPointer("map/a");
        Pointer pointerB = context.getPointer("map/b");

        assertFalse("Pointers for distinct map keys must not compare equal",
                pointerA.equals(pointerB));
        assertEquals(Boolean.TRUE, context.getValue("map/b != map/a"));
    }

    public void testNullPropertyAtFirstIndexIsReturnedByIterator() {
        NullHolder holder = new NullHolder();
        JXPathContext context = JXPathContext.newContext(holder);

        assertNull(context.getValue("nothing"));

        Iterator values = context.iterate("nothing[1]");
        assertTrue("A declared property with a null value is still selectable",
                values.hasNext());
        assertNull(values.next());
        assertFalse(values.hasNext());
    }

    public void testIndexedCollectionPointerUpdatesImmediateNodeWhenIndexChanges() {
        ListHolder holder = new ListHolder();
        holder.getItems().add("first");
        holder.getItems().add("second");

        JXPathContext context = JXPathContext.newContext(holder);
        PropertyPointer pointer = getPropertyPointer(context, "items[1]");

        assertTrue(pointer.isActual());
        assertTrue(pointer.isCollection());
        assertEquals(2, pointer.getLength());
        assertEquals("first", pointer.getImmediateNode());

        pointer.setIndex(1);

        assertEquals("Changing the selected index must select the second item",
                "second", pointer.getImmediateNode());
        assertTrue(pointer.isLeaf());
    }

    public void testScalarPropertyPointerExposesPropertyMetadataAndValue() {
        ScalarHolder holder = new ScalarHolder("alpha");
        JXPathContext context = JXPathContext.newContext(holder);

        PropertyPointer pointer = getPropertyPointer(context, "title");

        assertTrue(pointer.isActual());
        assertSame(holder, pointer.getBean());
        assertEquals("title", pointer.getPropertyName());
        assertEquals("alpha", pointer.getImmediateNode());
        assertEquals(1, pointer.getLength());
        assertFalse(pointer.isCollection());
        assertTrue(pointer.isLeaf());
    }

    private PropertyPointer getPropertyPointer(JXPathContext context, String xpath) {
        Pointer pointer = context.getPointer(xpath);
        assertTrue("Expected a PropertyPointer for XPath: " + xpath,
                pointer instanceof PropertyPointer);
        return (PropertyPointer) pointer;
    }

    public static class MapHolder {
        private final Map map = new HashMap();

        public Map getMap() {
            return map;
        }
    }

    public static class NullHolder {
        public Object getNothing() {
            return null;
        }
    }

    public static class ListHolder {
        private final List items = new ArrayList();

        public List getItems() {
            return items;
        }
    }

    public static class ScalarHolder {
        private final String title;

        public ScalarHolder(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
}
```

- `testMapPropertiesWithDifferentNamesAreDifferentPointersAndValues` targets JXPATH-151 map-property equality behavior. Distinct map keys must produce distinct pointers, and XPath comparison of `"b"` and `"a"` must evaluate to `true`.
- `testNullPropertyAtFirstIndexIsReturnedByIterator` targets the reported null-property regression. A declared JavaBean property whose value is `null` must remain selectable as `nothing[1]` and yield one null value.
- `testIndexedCollectionPointerUpdatesImmediateNodeWhenIndexChanges` exercises indexed collection access, collection detection, length calculation, leaf detection, and invalidation of the cached immediate value after an index change.
- `testScalarPropertyPointerExposesPropertyMetadataAndValue` covers normal scalar-property behavior: actual-property detection, bean lookup, property name, immediate value, scalar length, non-collection status, and atomic leaf status.