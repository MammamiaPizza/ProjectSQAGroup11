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