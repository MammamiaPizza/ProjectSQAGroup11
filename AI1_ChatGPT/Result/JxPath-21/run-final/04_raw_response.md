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
import org.apache.commons.jxpath.ri.QName;

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

    public void testMissingPropertyPointerIsNotActual() {
        JXPathContext context = JXPathContext.newContext(new ScalarHolder("alpha"));

        PropertyPointer pointer = getPropertyPointer(context, "missing");

        assertFalse("A property not declared by the bean must not be actual",
                pointer.isActual());
    }

    public void testNullPropertyPointerIsLeafAndNotCollection() {
        JXPathContext context = JXPathContext.newContext(new NullHolder());

        PropertyPointer pointer = getPropertyPointer(context, "nothing");

        assertTrue(pointer.isActual());
        assertNull(pointer.getImmediateNode());
        assertTrue("A null property value is a leaf", pointer.isLeaf());
        assertFalse("A null property value is not a collection",
                pointer.isCollection());
    }

    public void testWholeCollectionAndFirstElementPointersCompareEqual() {
        ListHolder holder = new ListHolder();
        holder.getItems().add("first");
        holder.getItems().add("second");

        JXPathContext context = JXPathContext.newContext(holder);
        PropertyPointer wholeCollection = getPropertyPointer(context, "items");
        PropertyPointer firstElement = getPropertyPointer(context, "items[1]");
        PropertyPointer secondElement = getPropertyPointer(context, "items[2]");

        assertTrue("A whole collection pointer represents its first element "
                + "for PropertyPointer equality", wholeCollection.equals(firstElement));
        assertEquals(wholeCollection.hashCode(), firstElement.hashCode());
        assertFalse(wholeCollection.equals(secondElement));
        assertFalse(wholeCollection.equals("items"));
    }

    public void testPropertyPointerExposesQNameAndImmediateValuePointer() {
        JXPathContext context = JXPathContext.newContext(new ScalarHolder("alpha"));

        PropertyPointer pointer = getPropertyPointer(context, "title");

        assertEquals("title", pointer.getName().toString());
        assertNotNull("A selected property value must have a value pointer",
                pointer.getImmediateValuePointer());
    }

    public void testCreatePathForExistingPropertyAndSetValue() {
        MutableHolder holder = new MutableHolder("alpha", "second");
        JXPathContext context = JXPathContext.newContext(holder);
        PropertyPointer pointer = getPropertyPointer(context, "first");

        assertSame("An already existing property needs no factory creation",
                pointer, pointer.createPath(context));

        assertSame(pointer, pointer.createPath(context, "beta"));
        assertEquals("beta", holder.getFirst());
    }

    public void testCreateChildCanUseSpecifiedOrInheritedPropertyName() {
        MutableHolder holder = new MutableHolder("first", "second");
        JXPathContext context = JXPathContext.newContext(holder);
        PropertyPointer first = getPropertyPointer(context, "first");

        PropertyPointer renamedChild = (PropertyPointer) first.createChild(
                context, new QName(null, "second"), 0, "updated");
        assertEquals("second", renamedChild.getPropertyName());
        assertEquals("updated", holder.getSecond());

        PropertyPointer inheritedNameChild = (PropertyPointer) first.createChild(
                context, null, 0);
        assertEquals("first", inheritedNameChild.getPropertyName());
        assertEquals("first", holder.getFirst());
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

    public static class MutableHolder {
        private String first;
        private String second;

        public MutableHolder(String first, String second) {
            this.first = first;
            this.second = second;
        }

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getSecond() {
            return second;
        }

        public void setSecond(String second) {
            this.second = second;
        }
    }
}
```

New test coverage targets:

- `testMissingPropertyPointerIsNotActual`
  - Covers the `isActual()` path where `isActualProperty()` is false.
  - Verifies that a non-existent bean property is not reported as an actual node.

- `testNullPropertyPointerIsLeafAndNotCollection`
  - Covers `isLeaf()` when `getNode()` is `null`.
  - Covers the false branch of `isCollection()` when the base value is `null`.
  - Complements the bug-related null-property iterator test by checking direct pointer semantics.

- `testWholeCollectionAndFirstElementPointersCompareEqual`
  - Covers `equals()` identity-independent comparison logic, including:
    - comparison of two `PropertyPointer` instances with equal parents and property metadata,
    - normalization of `WHOLE_COLLECTION` and index zero,
    - unequal indexes,
    - the non-`PropertyPointer` false branch.
  - Covers `hashCode()`.
  - Exercises equality behavior closely related to JXPATH-151, where distinct property names must remain distinct while equivalent selections must compare equal.

- `testPropertyPointerExposesQNameAndImmediateValuePointer`
  - Covers `getName()`.
  - Covers `getImmediateValuePointer()`.

- `testCreatePathForExistingPropertyAndSetValue`
  - Covers `createPath(JXPathContext)` when the selected property already has a non-null value, avoiding factory creation.
  - Covers `createPath(JXPathContext, Object)` where no collection expansion is needed and the property value is set.

- `testCreateChildCanUseSpecifiedOrInheritedPropertyName`
  - Covers both `createChild(...)` overloads.
  - Covers the `name != null` and `name == null` branches.
  - Verifies that a supplied child name selects and updates that property, while a null child name preserves the original property name.