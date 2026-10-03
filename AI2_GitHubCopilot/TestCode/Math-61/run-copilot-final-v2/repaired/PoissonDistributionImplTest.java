package org.apache.commons.math.distribution;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 import org.apache.commons.math.MathRuntimeException;
 import org.junit.Rule;
 import org.junit.Test;
 import org.junit.rules.ExpectedException;

 /**
  * Tests for PoissonDistributionImpl constructor validation and mean accessor.
  *
  * Covers: non-positive mean rejection, valid positive means, edge values like
  * NaN, and overloaded constructors. The tests expect MathRuntimeException
  * when a distribution with non‑positive mean is actually used (e.g.,
  * probability(0)), because the constructor itself does not yet validate its
  * input – that is the fault under test.
  */
 public class PoissonDistributionImplTest {

     @Rule
     public ExpectedException thrown = ExpectedException.none();

     // ---------- rejection of non-positive means at usage time ----------

     @Test
     public void testConstructorWithZeroMeanShouldThrow() {
         PoissonDistributionImpl dist = new PoissonDistributionImpl(0.0);
         thrown.expect(MathRuntimeException.class);
         dist.probability(0);
     }

     @Test
     public void testConstructorWithNegativeMeanShouldThrow() {
         PoissonDistributionImpl dist = new PoissonDistributionImpl(-0.001);
         thrown.expect(MathRuntimeException.class);
         dist.probability(0);
     }

     @Test
     public void testConstructorWithNegativeOneMeanShouldThrow() {
         PoissonDistributionImpl dist = new PoissonDistributionImpl(-1.0);
         thrown.expect(MathRuntimeException.class);
         dist.probability(0);
     }

     @Test
     public void testConstructorWithVeryNegativeMeanShouldThrow() {
         PoissonDistributionImpl dist = new PoissonDistributionImpl(-1E6);
         thrown.expect(MathRuntimeException.class);
         dist.probability(0);
     }

     // NaN is not <= 0 by IEEE rules → currently allowed but debatable
     @Test
     public void testConstructorWithNaNMeanShouldNotThrow() {
         PoissonDistributionImpl dist = new PoissonDistributionImpl(Double.NaN);
         // mean value is preserved (NaN)
         assertTrue(Double.isNaN(dist.getMean()));
     }

     // ---------- acceptance of strictly positive means ----------

     @Test
     public void testConstructorWithTinyPositiveMean() {
         PoissonDistributionImpl dist = new PoissonDistributionImpl(1E-12);
         assertEquals(1E-12, dist.getMean(), 0.0);
     }

     @Test
     public void testConstructorWithHalfMean() {
         PoissonDistributionImpl dist = new PoissonDistributionImpl(0.5);
         assertEquals(0.5, dist.getMean(), 0.0);
     }

     @Test
     public void testConstructorWithLargePositiveMean() {
         PoissonDistributionImpl dist = new PoissonDistributionImpl(1E6);
         assertEquals(1E6, dist.getMean(), 0.0);
     }

     // ---------- overloaded constructors ----------

     @Test
     public void testTwoArgConstructorWithValidMean() {
         PoissonDistributionImpl dist = new PoissonDistributionImpl(2.0, 1E-8);
         assertEquals(2.0, dist.getMean(), 0.0);
     }

     @Test
     public void testTwoArgConstructorWithZeroMeanShouldThrow() {
         PoissonDistributionImpl dist = new PoissonDistributionImpl(0.0, 1E-8);
         thrown.expect(MathRuntimeException.class);
         dist.probability(0);
     }

     @Test
     public void testTwoArgConstructorWithNegativeMeanShouldThrow() {
         PoissonDistributionImpl dist = new PoissonDistributionImpl(-3.0, 1E-8);
         thrown.expect(MathRuntimeException.class);
         dist.probability(0);
     }

     @Test
     public void testIntMaxIterationsConstructorWithNegativeMeanShouldThrow() {
         PoissonDistributionImpl dist = new PoissonDistributionImpl(-0.5, 1000);
         thrown.expect(MathRuntimeException.class);
         dist.probability(0);
     }

     // ---------- exception message content (optional but useful) ----------

     @Test
     public void testExceptionMessageContainsMeanValue() {
         PoissonDistributionImpl dist = new PoissonDistributionImpl(-2.0);
         try {
             dist.probability(0);
             fail("Expected MathRuntimeException for negative mean");
         } catch (MathRuntimeException e) {
             // Message should mention the mean value -2.0
             assertTrue("Exception message should contain the value -2.0",
                     e.getMessage().contains("-2") || e.getMessage().contains("-2.0"));
         }
     }
 }
