package org.jfree.data.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.jfree.data.KeyedObjects2D;
import org.jfree.data.UnknownKeyException;
import org.junit.Test;

public class KeyedObjects2DBug22Test {

    @Test
    public void testSetObjectCreatesAndUpdatesKeyedCell() {
        KeyedObjects2D data = new KeyedObjects2D();

        data.setObject(Integer.valueOf(1), "R1", "C1");
        data.setObject(Integer.valueOf(2), "R2", "C2");
        data.setObject(Integer.valueOf(3), "R1", "C1");

        assertEquals(2, data.getRowCount());
        assertEquals(2, data.getColumnCount());
        assertEquals(Integer.valueOf(3), data.getObject("R1", "C1"));
        assertEquals(Integer.valueOf(2), data.getObject("R2", "C2"));
        assertEquals(1, data.getColumnIndex("C2"));
    }

    @Test
    public void testGetObjectByKeyReturnsNullForMissingCellInKnownRowAndColumn() {
        KeyedObjects2D data = new KeyedObjects2D();
        data.addObject(Integer.valueOf(1), "R1", "C1");
        data.addObject(Integer.valueOf(2), "R2", "C2");

        assertNull(data.getObject("R1", "C2"));
        assertNull(data.getObject("R2", "C1"));
    }

    @Test
    public void testGetObjectByKeyRejectsUnknownKeys() {
        KeyedObjects2D data = new KeyedObjects2D();
        data.addObject(Integer.valueOf(1), "R1", "C1");

        try {
            data.getObject("R2", "C1");
            fail("Expected UnknownKeyException for an unknown row key.");
        } catch (UnknownKeyException expected) {
            // expected
        }

        try {
            data.getObject("R1", "C2");
            fail("Expected UnknownKeyException for an unknown column key.");
        } catch (UnknownKeyException expected) {
            // expected
        }
    }

    @Test
    public void testRemoveObjectRemovesEmptyRowAndEmptyColumn() {
        KeyedObjects2D data = new KeyedObjects2D();
        data.addObject(Integer.valueOf(1), "R1", "C1");

        data.removeObject("R1", "C1");

        assertEquals(0, data.getRowCount());
        assertEquals(0, data.getColumnCount());
        assertEquals(-1, data.getRowIndex("R1"));
        assertEquals(-1, data.getColumnIndex("C1"));
    }

    @Test
    public void testRemoveObjectRetainsColumnWhileAnotherRowHasValue() {
        KeyedObjects2D data = new KeyedObjects2D();
        data.addObject(Integer.valueOf(1), "R1", "C1");
        data.addObject(Integer.valueOf(2), "R2", "C1");
        data.addObject(Integer.valueOf(3), "R2", "C2");

        data.removeObject("R1", "C1");

        assertEquals(1, data.getRowCount());
        assertEquals(2, data.getColumnCount());
        assertEquals(-1, data.getRowIndex("R1"));
        assertEquals(Integer.valueOf(2), data.getObject("R2", "C1"));
        assertEquals(Integer.valueOf(3), data.getObject("R2", "C2"));
    }

    @Test
    public void testRemoveColumnByIndexRemovesOnlySpecifiedColumn() {
        KeyedObjects2D data = new KeyedObjects2D();
        data.addObject(Integer.valueOf(1), "R1", "C1");
        data.addObject(Integer.valueOf(2), "R1", "C2");
        data.addObject(Integer.valueOf(3), "R2", "C2");

        data.removeColumn(0);

        assertEquals(1, data.getColumnCount());
        assertEquals("C2", data.getColumnKey(0));
        assertEquals(-1, data.getColumnIndex("C1"));
        assertEquals(Integer.valueOf(2), data.getObject("R1", "C2"));
        assertEquals(Integer.valueOf(3), data.getObject("R2", "C2"));
    }

    @Test
    public void testRemoveColumnByKeyRemovesOnlySpecifiedColumn() {
        KeyedObjects2D data = new KeyedObjects2D();
        data.addObject(Integer.valueOf(1), "R1", "C1");
        data.addObject(Integer.valueOf(2), "R1", "C2");

        data.removeColumn("C2");

        assertEquals(1, data.getColumnCount());
        assertEquals("C1", data.getColumnKey(0));
        assertEquals(Integer.valueOf(1), data.getObject("R1", "C1"));
        assertEquals(-1, data.getColumnIndex("C2"));
    }

    @Test
    public void testRemoveRowByKeyRemovesExistingRow() {
        KeyedObjects2D data = new KeyedObjects2D();
        data.addObject(Integer.valueOf(1), "R1", "C1");
        data.addObject(Integer.valueOf(2), "R2", "C1");

        data.removeRow("R1");

        assertEquals(1, data.getRowCount());
        assertEquals("R2", data.getRowKey(0));
        assertEquals(-1, data.getRowIndex("R1"));
        assertEquals(Integer.valueOf(2), data.getObject("R2", "C1"));
    }

    @Test
    public void testRemoveRowByUnknownKeyThrowsUnknownKeyException() {
        KeyedObjects2D data = new KeyedObjects2D();
        data.addObject(Integer.valueOf(1), "R1", "C1");

        try {
            data.removeRow("R2");
            fail("Expected UnknownKeyException for an unknown row key.");
        } catch (UnknownKeyException expected) {
            // expected
        }
    }

@org.junit.Test
public void testEqualsAndHashCodeForNullAndNonNullCells() {
    org.jfree.data.KeyedObjects2D data1 = new org.jfree.data.KeyedObjects2D();
    data1.setObject(Integer.valueOf(7), "R1", "C1");
    data1.setObject(null, "R1", "C2");

    org.jfree.data.KeyedObjects2D data2 = new org.jfree.data.KeyedObjects2D();
    data2.setObject(Integer.valueOf(7), "R1", "C1");
    data2.setObject(null, "R1", "C2");

    org.junit.Assert.assertTrue(data1.equals(data2));
    org.junit.Assert.assertEquals(data1.hashCode(), data2.hashCode());

    data2.setObject(Integer.valueOf(8), "R1", "C2");
    org.junit.Assert.assertFalse(data1.equals(data2));
}

@org.junit.Test
public void testCloneIsIndependentOfOriginalRows() throws CloneNotSupportedException {
    org.jfree.data.KeyedObjects2D original = new org.jfree.data.KeyedObjects2D();
    original.setObject(Integer.valueOf(1), "R1", "C1");
    original.setObject(Integer.valueOf(2), "R2", "C1");

    org.jfree.data.KeyedObjects2D clone
            = (org.jfree.data.KeyedObjects2D) original.clone();
    org.junit.Assert.assertTrue(original.equals(clone));

    clone.removeObject("R1", "C1");

    org.junit.Assert.assertEquals(2, original.getRowCount());
    org.junit.Assert.assertEquals(Integer.valueOf(1), original.getObject("R1", "C1"));
    org.junit.Assert.assertEquals(1, clone.getRowCount());
    org.junit.Assert.assertEquals(Integer.valueOf(2), clone.getObject("R2", "C1"));
}

@org.junit.Test
public void testRemoveRowByIndexRemovesOnlySelectedRow() {
    org.jfree.data.KeyedObjects2D data = new org.jfree.data.KeyedObjects2D();
    data.setObject(Integer.valueOf(1), "R1", "C1");
    data.setObject(Integer.valueOf(2), "R2", "C1");

    data.removeRow(0);

    org.junit.Assert.assertEquals(1, data.getRowCount());
    org.junit.Assert.assertEquals("R2", data.getRowKey(0));
    org.junit.Assert.assertEquals(Integer.valueOf(2), data.getObject(0, 0));
}

@org.junit.Test(expected = org.jfree.data.UnknownKeyException.class)
public void testRemoveColumnByUnknownKeyThrowsUnknownKeyException() {
    org.jfree.data.KeyedObjects2D data = new org.jfree.data.KeyedObjects2D();

    data.removeColumn("C1");
}
}
