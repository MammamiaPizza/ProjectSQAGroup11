package org.apache.commons.math3.distribution;

import org.junit.Test;
import static org.junit.Assert.*;

public class FDistributionUniformRealDistributionSupportInclusivityTest {

 @Test
 public void testFDistributionSupportLowerBoundNotInclusive() {
     FDistribution f = new FDistribution(1, 1);
     assertFalse("Support lower bound (0) should not be inclusive",
f.isSupportLowerBoundInclusive());
 }

 @Test
 public void testFDistributionSupportUpperBoundNotInclusive() {
     FDistribution f = new FDistribution(2, 5);
     assertFalse("Support upper bound (infinity) should not be inclusive",
f.isSupportUpperBoundInclusive());
 }

 @Test
 public void testFDistributionSupportConnected() {
     FDistribution f = new FDistribution(5, 5);
     assertTrue("F-distribution support should be connected", f.isSupportConnected());
 }

 @Test
 public void testFDistributionSupportLowerBoundIsZero() {
     FDistribution f = new FDistribution(10, 20);
     assertEquals(0.0, f.getSupportLowerBound(), 0.0);
 }

 @Test
 public void testFDistributionSupportUpperBoundIsInfinity() {
     FDistribution f = new FDistribution(3, 7);
     assertEquals(Double.POSITIVE_INFINITY, f.getSupportUpperBound(), 0.0);
 }

 @Test
 public void testFDistributionSupportLowerBoundInclusiveIndependentOfDOF() {
     FDistribution f1 = new FDistribution(3, 7);
     FDistribution f2 = new FDistribution(10, 100);
     assertFalse(f1.isSupportLowerBoundInclusive());
     assertFalse(f2.isSupportLowerBoundInclusive());
 }

 @Test
 public void testUniformRealDistributionSupportLowerBoundInclusive() {
     UniformRealDistribution u = new UniformRealDistribution(-1, 1);
     assertTrue("Support lower bound should be inclusive", u.isSupportLowerBoundInclusive());
 }

 @Test
 public void testUniformRealDistributionSupportUpperBoundInclusive() {
     UniformRealDistribution u = new UniformRealDistribution(0, 1);
     assertTrue("Support upper bound should be inclusive", u.isSupportUpperBoundInclusive());
 }

 @Test
 public void testUniformRealDistributionSupportConnected() {
     UniformRealDistribution u = new UniformRealDistribution(-5, 5);
     assertTrue(u.isSupportConnected());
 }

 @Test
 public void testUniformRealDistributionSupportBoundsMatchConstructor() {
     UniformRealDistribution u = new UniformRealDistribution(3.5, 9.8);
     assertEquals(3.5, u.getSupportLowerBound(), 0.0);
     assertEquals(9.8, u.getSupportUpperBound(), 0.0);
 }

 @Test
 public void testUniformRealDistributionDefaultSupportBoundInclusivity() {
     UniformRealDistribution u = new UniformRealDistribution();
     assertTrue(u.isSupportLowerBoundInclusive());
     assertTrue(u.isSupportUpperBoundInclusive());
 }

 @Test
 public void testUniformRealDistributionExtremeRangeSupportBoundInclusivity() {
     UniformRealDistribution u = new UniformRealDistribution(1e-9, 1e9);
     assertTrue(u.isSupportLowerBoundInclusive());
     assertTrue(u.isSupportUpperBoundInclusive());
 }

}
