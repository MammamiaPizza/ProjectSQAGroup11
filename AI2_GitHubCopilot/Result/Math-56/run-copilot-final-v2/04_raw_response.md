@Test
    public void testToStringNotEmpty() {
        MultidimensionalCounter counter = new MultidimensionalCounter(3, 2);
        String s = counter.toString();
        assertNotNull(s);
        assertFalse(s.isEmpty());
    }

 @Test
 public void testIterationConsistency2D_3x2AndGetCountDim() {
     MultidimensionalCounter counter = new MultidimensionalCounter(3, 2);
     int[][] expectedCounts = { {0,0}, {0,1}, {1,0}, {1,1}, {2,0}, {2,1} };
     int idx = 0;
     for (MultidimensionalCounter.Iterator it = counter.iterator(); it.hasNext();) {
         it.next();
         assertEquals(idx, it.getCount());
         assertArrayEquals(expectedCounts[idx], it.getCounts());
         assertEquals(expectedCounts[idx][0], it.getCount(0));
         assertEquals(expectedCounts[idx][1], it.getCount(1));
         idx++;
     }
     assertEquals(6, idx);
 }