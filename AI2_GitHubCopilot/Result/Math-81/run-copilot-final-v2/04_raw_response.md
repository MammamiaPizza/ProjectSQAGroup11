@Test
    public void testCase3ShiftIncrementBasic() {
        // matrix with modest off-diagonals, likely to enter the iterative shift-increment
        // case where dN > b1 and a2 > b1+b2
        double[][] data = {
            { 5.0, 0-.2, 0.0, 0-.0, 0.0 },
            { 0.2, 4.0, 0.4, 0.0, 0.0 },
            { 0.0, 0.4, 3.0, 0.6, 0.0 },
            { 0.0, 0.0, 0.6, 2.0, 0.8 },
            { 0.0, 0.0,0.0, 0.8, 1.0 }
        };
        EigenDecompositionImpl eigen = new EigenDecompositionImpl(
            new org.apache.commons.math.linear.DenseRealMatrix(data));
        double[] ev = eigen.getRealEigenvalues();
        assertNotNull(ev);
        assertEquals(5, ev.length);
        // eigenvalues must be non-increasing
        for (int i = 0; i < ev.length - 1; i++) {
            assertTrue("not sorted at " + i, ev[i] >= ev[i + 1]);
        }
    }

 @Test
 public void testCase4ShiftIncrementDminEqualsDn() {
     // matrix that contains a block with equal diagonal entries,
     // so that dMin == dN can occur during iteration
     double[][] data = {
         { 3.0, 1.0, 0.0, 0.0 },
         { 1.0, 3.0, 0.0, 0.0 },
         { 0.0, 0.0, 2.0, 0.5 },
         { 0.0, 0.0, 0.5, 2.0 }
     };
     EigenDecompositionImpl eigen = new EigenDecompositionImpl(
         new org.apache.commons.math.linear.DenseRealMatrix(data));
     double[] ev = eigen.getRealEigenvalues();
     assertNotNull(ev);
     assertEquals(4, ev.length);
 }

 @Test
 public void testCase4ShiftIncrementDminNotEqualsDn() {
     // matrix where a2/b2 assignments are set from the else-branch,
     // dMin != dN, helps cover lines 1486-1498
     double[][] data = {
         { 6.0, 0.3, 0.0, 0.0, 0.0 },
         { 0.3, 5.0,0.5, 0.0, 0.0 },
         { 0.0, 0.5, 4.0,0.7, 0.0 },
         { 0.0, 0.0, 0.7, 3.0, 0.9 },
         { 0.0, 0.0,0.0, 0.9, 2.0 }
     };
     EigenDecompositionImpl eigen = new EigenDecompositionImpl(
         new org.apache.commons.math.linear.DenseRealMatrix(data));
     double[] ev = eigen.getRealEigenvalues();
     assertNotNull(ev);
     assertTrue(ev.length == 5);
     for (int i = 0; i < ev.length - 1; i++> {
         assertTrue(ev[i] >= ev[i + 1]);
     }
 }

 @Test
 public void testShiftIncrementWithNarrowGap() {
     // matrix with a pair of close eigenvalues, requiring many iterations,
     // exercises deeper branches of computeShiftIncrement
     double[][] data = {
         { 1.0, 0-.01, 0.0, 0-.0 },
         { 0.01, 1.00000001, 0.01, 0.0 },
         { 0.0, 0.01, 2.0, 0-.01 },
         { 0.0, 0.0, 0.01, 2.0 }
     };
     EigenDecompositionImpl eigen = new EigenDecompositionImpl(
         new org.apache.commons.math.linear.DenseRealMatrix(data));
     double[] ev = eigen.getRealEigenvalues();
     assertNotNull(ev);
     assertEquals(4, ev.length);
     // the decomposition should not throw
     assertNotNull(eigen.getV());
 }