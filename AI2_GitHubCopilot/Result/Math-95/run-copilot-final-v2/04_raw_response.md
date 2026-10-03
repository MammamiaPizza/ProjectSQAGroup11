@Test
 public void testGetInitialDomainDenomDfGreaterThanTwo() {
     FDistributionImpl dist = new FDistributionImpl(4.0, 6.0);
     double initial = dist.getInitialDomain(0.5);
     assertEquals(6.0 / (6.0 - 2.0), initial, 1e-10);
 }

 @Test
 public void testGetInitialDomainDenomDfExactlyTwo() {
     FDistributionImpl dist = new FDistributionImpl(4.0, 2.0);
     double initial = dist.getInitialDomain(0.5);
     assertTrue("Initial domain for denom df=2 must be non-negative", initial >= 0.0);
 }

 @Test
 public void testInverseWithDenomDfLessThanTwo() {
     FDistributionImpl dist = new FDistributionImpl(3.0, 1.5);
     double x = dist.inverseCumulativeProbability(0.5);
     assertTrue("Inverse for small denom df must be non-negative", x >= 0.0);
 }

 @Test
 public void testGetInitialDomainJustAboveTwo() {
     FDistributionImpl dist = new FDistributionImpl(5.0, 2.0001);
     double initial = dist.getInitialDomain(0.5);
     assertTrue("Initial domain just above df=2 must be positive", initial > 0.0);
 }