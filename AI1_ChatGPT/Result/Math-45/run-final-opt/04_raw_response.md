@org.junit.Test
public void testCopyRetainsEntriesAndIsIndependent() {
    OpenMapRealMatrix original = new OpenMapRealMatrix(2, 3);
    original.setEntry(1, 2, 7.5);

    OpenMapRealMatrix copy = original.copy();

    org.junit.Assert.assertEquals(7.5, copy.getEntry(1, 2), 0.0);
    copy.setEntry(1, 2, -2.0);
    org.junit.Assert.assertEquals(7.5, original.getEntry(1, 2), 0.0);
    org.junit.Assert.assertEquals(-2.0, copy.getEntry(1, 2), 0.0);
}

@org.junit.Test
public void testCreateMatrixCreatesEmptyRequestedDimensions() {
    OpenMapRealMatrix matrix = new OpenMapRealMatrix(1, 1);

    OpenMapRealMatrix created = matrix.createMatrix(3, 2);

    org.junit.Assert.assertEquals(3, created.getRowDimension());
    org.junit.Assert.assertEquals(2, created.getColumnDimension());
    org.junit.Assert.assertEquals(0.0, created.getEntry(2, 1), 0.0);
}

@org.junit.Test
public void testAddCombinesSparseEntriesWithoutChangingOperands() {
    OpenMapRealMatrix left = new OpenMapRealMatrix(2, 3);
    left.setEntry(0, 1, 4.0);
    left.setEntry(1, 2, -1.0);

    OpenMapRealMatrix right = new OpenMapRealMatrix(2, 3);
    right.setEntry(0, 1, 3.0);
    right.setEntry(1, 0, 5.0);

    OpenMapRealMatrix sum = left.add(right);

    org.junit.Assert.assertEquals(7.0, sum.getEntry(0, 1), 0.0);
    org.junit.Assert.assertEquals(5.0, sum.getEntry(1, 0), 0.0);
    org.junit.Assert.assertEquals(-1.0, sum.getEntry(1, 2), 0.0);
    org.junit.Assert.assertEquals(4.0, left.getEntry(0, 1), 0.0);
    org.junit.Assert.assertEquals(3.0, right.getEntry(0, 1), 0.0);
}

@org.junit.Test
public void testEntryUpdatesHandleZeroAndNonZeroResults() {
    OpenMapRealMatrix matrix = new OpenMapRealMatrix(2, 2);
    matrix.setEntry(1, 1, 5.0);

    matrix.addToEntry(1, 1, -5.0);
    org.junit.Assert.assertEquals(0.0, matrix.getEntry(1, 1), 0.0);

    matrix.addToEntry(1, 1, 3.0);
    org.junit.Assert.assertEquals(3.0, matrix.getEntry(1, 1), 0.0);

    matrix.multiplyEntry(1, 1, 2.0);
    org.junit.Assert.assertEquals(6.0, matrix.getEntry(1, 1), 0.0);

    matrix.multiplyEntry(1, 1, 0.0);
    org.junit.Assert.assertEquals(0.0, matrix.getEntry(1, 1), 0.0);
}