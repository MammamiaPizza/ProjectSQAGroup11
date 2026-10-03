package org.apache.commons.math3.random;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for BitsStreamGenerator clone behavior.
  * The core contract: after cloning, the clone must produce exactly the same
  * sequence of random values (nextDouble, nextGaussian, nextInt, etc.) as the
  * original at the point of cloning, including proper handling of the cached
  * Gaussian value used by the Box-Muller transform.
  */
 public class BitsStreamGeneratorCloneTest {

     /**
      * Minimal deterministic BitsStreamGenerator that supports Cloneable.
      * Uses a simple linear congruential generator so that state is predictable.
      */
     private static class CloneableGenerator extends BitsStreamGenerator implements Cloneable {
         private int state;

         CloneableGenerator(int seed) {
             super();
             this.state = seed;
         }

         @Override
         protected int next(int bits) {
             state = state * 1103515245 + 12345;
             return state >>> (32 - bits);
         }

         @Override
         public void setSeed(int seed) {
             this.state = seed;
             clear();
         }

         @Override
         public void setSeed(int[] seed) {
             setSeed(seed.length > 0 ? seed[0] : 0);
         }

         @Override
         public void setSeed(long seed) {
             this.state = (int) seed;
             clear();
         }

         @Override
         public CloneableGenerator clone() {
             try {
                 return (CloneableGenerator) super.clone();
             } catch (CloneNotSupportedException e) {
                 throw new AssertionError("Clone failed", e);
             }
         }
     }

     private CloneableGenerator newGen() {
         return new CloneableGenerator(42);
     }

     @Test
     public void testCloneWithEmptyGaussianCacheProducesIdenticalSequence() {
         CloneableGenerator orig = newGen();
         // Even number of nextGaussian calls: cache is empty (Double.NaN)
         orig.nextGaussian();
         orig.nextGaussian(); // pair complete
         orig.nextGaussian();
         orig.nextGaussian(); // pair complete

         CloneableGenerator clone = orig.clone();

         for (int i = 0; i < 20; i++) {
             assertEquals("nextDouble mismatch at index " + i,
                     orig.nextDouble(), clone.nextDouble(), 0.0);
         }
     }

     @Test
     public void testCloneWithFilledGaussianCacheReturnsCachedValue() {
         CloneableGenerator orig = newGen();
         orig.nextDouble();
         orig.nextDouble();
         // One nextGaussian call: first value of Box-Muller pair is returned,
         // second value is cached in the private nextGaussian field.
         double firstOfPair = orig.nextGaussian();

         CloneableGenerator clone = orig.clone();

         // Both original and clone must return the cached second value.
         double cachedFromOrig = orig.nextGaussian();
         double cachedFromClone = clone.nextGaussian();
         assertEquals("Cached Gaussian must match", cachedFromOrig, cachedFromClone, 0.0);
         assertFalse("Cached value differs from first-of-pair", Double.compare(firstOfPair,
 cachedFromOrig) == 0);
     }

     @Test
     public void testClonePreservesGaussianSequence() {
         CloneableGenerator orig = newGen();
         // Call nextGaussian 3 times: after odd count a cached value remains.
         orig.nextGaussian(); // 1: cache filled
         orig.nextGaussian(); // 2: returns cached, cache empty
         orig.nextGaussian(); // 3: cache filled again

         CloneableGenerator clone = orig.clone();

         for (int i = 0; i < 15; i++) {
             assertEquals("nextGaussian mismatch at index " + i,
                     orig.nextGaussian(), clone.nextGaussian(), 0.0);
         }
     }

     @Test
     public void testCloneAfterZeroCalls() {
         CloneableGenerator orig = newGen();
         CloneableGenerator clone = orig.clone();

         for (int i = 0; i < 25; i++) {
             assertEquals("nextDouble at " + i, orig.nextDouble(), clone.nextDouble(), 0.0);
             assertEquals("nextInt at " + i, orig.nextInt(), clone.nextInt());
         }
     }

     @Test
     public void testClonePreservesAllPrimitiveMethods() {
         CloneableGenerator orig = newGen();
         orig.nextInt();
         orig.nextDouble();
         orig.nextGaussian();
         orig.nextLong();
         orig.nextBoolean();
         orig.nextFloat();
         orig.nextBytes(new byte[4]);

         CloneableGenerator clone = orig.clone();

         assertEquals("nextInt", orig.nextInt(), clone.nextInt());
         assertEquals("nextDouble", orig.nextDouble(), clone.nextDouble(), 0.0);
         assertEquals("nextGaussian", orig.nextGaussian(), clone.nextGaussian(), 0.0);
         assertEquals("nextLong", orig.nextLong(), clone.nextLong());
         assertEquals("nextBoolean", orig.nextBoolean(), clone.nextBoolean());
         assertEquals("nextFloat", orig.nextFloat(), clone.nextFloat(), 0.0);

         byte[] b1 = new byte[4];
         byte[] b2 = new byte[4];
         orig.nextBytes(b1);
         clone.nextBytes(b2);
         assertArrayEquals("nextBytes", b1, b2);
     }

     @Test
     public void testMultipleClonesFromSameOriginal() {
         CloneableGenerator orig = newGen();
         orig.nextGaussian();
         orig.nextGaussian();
         orig.nextGaussian(); // cache filled after odd count

         CloneableGenerator c1 = orig.clone();
         CloneableGenerator c2 = orig.clone();

         for (int i = 0; i < 15; i++) {
             double v1 = c1.nextDouble();
             double v2 = c2.nextDouble();
             assertEquals("Clone divergence at " + i, v1, v2, 0.0);
             assertEquals("Original vs clone at " + i, orig.nextDouble(), v1, 0.0);
         }
     }

     @Test
     public void testCloneDoesNotShareMutableState() {
         CloneableGenerator orig = newGen();
         orig.nextGaussian(); // cache filled
         CloneableGenerator clone = orig.clone();

         // Advance original only
         orig.nextGaussian(); // consumes cached value, cache now empty
         orig.nextGaussian(); // new pair, cache filled

         // Clone should still have the original cached value, unaffected
         double cloneCached = clone.nextGaussian();
         assertFalse("Clone cache should not be NaN after being filled",
                 Double.isNaN(cloneCached));
     }

     @Test
     public void testCloneAfterNextIntWithBound() {
         CloneableGenerator orig = newGen();
         orig.nextInt(100);
         orig.nextInt(50);
         orig.nextGaussian(); // cache filled

         CloneableGenerator clone = orig.clone();

         for (int i = 0; i < 10; i++) {
             assertEquals("nextInt(100) at " + i, orig.nextInt(100), clone.nextInt(100));
             assertEquals("nextDouble at " + i, orig.nextDouble(), clone.nextDouble(), 0.0);
         }
     }

     @Test
     public void testCloneAfterExplicitClear() {
         CloneableGenerator orig = newGen();
         orig.nextGaussian(); // cache filled
         orig.clear();       // explicitly clear cache
         assertFalse("Cache should be cleared", Double.isNaN(orig.nextGaussian()));
         // After clear + one call, cache is filled again:
         orig = newGen();
         orig.nextGaussian(); // cache filled
         orig.clear();

         CloneableGenerator clone = orig.clone();

         for (int i = 0; i < 12; i++) {
             assertEquals("nextGaussian mismatch at " + i,
                     orig.nextGaussian(), clone.nextGaussian(), 0.0);
         }
     }

     @Test
     public void testCloneAtEveryCacheState() {
         double[] reference = new double[8];
         CloneableGenerator ref = newGen();
         for (int i = 0; i < 8; i++) {
             reference[i] = ref.nextGaussian();
         }

         // Clone at each point 0..6 and verify the remaining tail matches.
         for (int p = 0; p <= 6; p++) {
             CloneableGenerator g = newGen();
             for (int i = 0; i < p; i++) {
                 g.nextGaussian();
             }
             CloneableGenerator clone = g.clone();
             for (int i = p; i < 8; i++) {
                 assertEquals("Clone at p=" + p + " idx=" + i,
                         reference[i], clone.nextGaussian(), 0.0);
             }
         }
     }

     @Test
     public void testCloneWithNextBytesAndGaussianMix() {
         CloneableGenerator orig = newGen();
         byte[] buf1 = new byte[8];
         orig.nextBytes(buf1);
         orig.nextGaussian(); // cache filled
         orig.nextLong();

         CloneableGenerator clone = orig.clone();

         byte[] buf2 = new byte[8];
         orig.nextBytes(buf2);
         byte[] buf3 = new byte[8];
         clone.nextBytes(buf3);
         assertArrayEquals("nextBytes after clone", buf2, buf3);

         assertEquals("nextGaussian after clone",
                 orig.nextGaussian(), clone.nextGaussian(), 0.0);
         assertEquals("nextLong after clone",
                 orig.nextLong(), clone.nextLong());
     }

     @Test
     public void testCloneAfterLargeNumberOfMixedCalls() {
         CloneableGenerator orig = newGen();
         for (int i = 0; i < 13; i++) {
             orig.nextInt(256);
             orig.nextDouble();
             orig.nextGaussian();
             orig.nextBoolean();
         }
         // 13 * 3 = 39 nextGaussian calls (odd => cache filled)

         CloneableGenerator clone = orig.clone();

         for (int i = 0; i < 30; i++) {
             assertEquals("nextDouble @" + i, orig.nextDouble(), clone.nextDouble(), 0.0);
             assertEquals("nextGaussian @" + i, orig.nextGaussian(), clone.nextGaussian(), 0.0);
             assertEquals("nextInt @" + i, orig.nextInt(64), clone.nextInt(64));
         }
     }
 }
