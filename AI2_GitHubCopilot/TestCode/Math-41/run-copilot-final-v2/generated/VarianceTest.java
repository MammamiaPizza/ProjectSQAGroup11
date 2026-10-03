package org.apache.commons.math.stat.descriptive.moment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math.exception.NotPositiveException;
import org.junit.Test;

public class VarianceTest {

 private static final double EPS = 1e-9;

 @Test
 public void testWeightedSegmentUnbiased() {
     Variance v = new Variance(true);
     double[] values = {1, 2, 3, 4, 5};
     double[] weights = {10, 1, 2, 3, 4};
     double result = v.evaluate(values, weights, 1, 3);
     // expected unbiased weighted variance for segment indices 1..3 (values 2,3,4, weights 1,2,3)
     double expected = 0.6666666666666666;
     assertEquals(expected, result, EPS);
 }

 @Test
 public void testWeightedSegmentPopulation() {
     Variance v = new Variance(false);
     double[] values = {1, 2, 3, 4, 5};
     double[] weights = {10, 1, 2, 3, 4};
     double result = v.evaluate(values, weights, 1, 3);
     double expected = 0.5555555555555556;
     assertEquals(expected, result, EPS);
 }

 @Test
 public void testWeightedSegmentWithMean() {
     Variance v = new Variance(true);
     double[] values = {1, 2, 3, 4, 5};
     double[] weights = {10, 1, 2, 3, 4};
     double mean = 3.333333333333333;
     double result = v.evaluate(values, weights, mean, 1, 3);
     double expected = 0.6666666666666666;
     assertEquals(expected, result, EPS);
 }

 @Test
 public void testWeightedSegmentSingleElement() {
     Variance v = new Variance();
     double[] values = {5};
     double[] weights = {2};
     double result = v.evaluate(values, weights, 0, 1);
     assertEquals(0.0, result, EPS);
 }

 @Test
 public void testWeightedSegmentZeroLength() {
     Variance v = new Variance();
     double[] values = {1, 2, 3};
     double[] weights = {1, 1, 1};
     double result = v.evaluate(values, weights, 0, 0);
     assertTrue(Double.isNaN(result));
 }

 @Test
 public void testWeightedSegmentEqualToSubArray() {
     // Regression: segment evaluation should equal full-array evaluation of extracted sub-arrays
     Variance v = new Variance(true);
     double[] fullValues = {10, 20, 30, 40, 50, 60};
     double[] fullWeights = {0.5, 1.5, 2, 3, 4, 5};
     int begin = 2;
     int length = 3;
     double segmentResult = v.evaluate(fullValues, fullWeights, begin, length);
     double[] subValues = new double[length];
     double[] subWeights = new double[length];
     System.arraycopy(fullValues, begin, subValues, 0, length);
     System.arraycopy(fullWeights, begin, subWeights, 0, length);
     double subResult = v.evaluate(subValues, subWeights);
     assertEquals(subResult, segmentResult, EPS);
 }

 @Test
 public void testWeightedSegmentEqualToSubArrayGivenMean() {
     Variance v = new Variance(true);
     double[] values = {2, 4, 6, 8, 10};
     double[] weights = {0.5, 1, 2, 3, 1};
     int begin = 1;
     int length = 3;
     double mean = 7.0; // arbitrary, but same used for both
     double segmentResult = v.evaluate(values, weights, mean, begin, length);
     double[] subVals = new double[length];
     double[] subWts = new double[length];
     System.arraycopy(values, begin, subVals, 0, length);
     System.arraycopy(weights, begin, subWts, 0, length);
     double subResult = v.evaluate(subVals, subWts, mean);
     assertEquals(subResult, segmentResult, EPS);
 }

 @Test
 public void testWeightedSegmentVsUnweightedWithUnitWeights() {
     Variance wv = new Variance(true);
     Variance uv = new Variance(true);
     double[] values = {5, 6, 7, 8, 9};
     double[] unitWeights = {1.0, 1.0, 1.0, 1.0, 1.0};
     int begin = 1;
     int length = 3;
     double weightedResult = wv.evaluate(values, unitWeights, begin, length);
     double unweightedResult = uv.evaluate(values, begin, length);
     assertEquals(unweightedResult, weightedResult, EPS);
 }

 @Test(expected = NotPositiveException.class)
 public void testNegativeWeightThrows() {
     Variance v = new Variance();
     double[] values = {1, 2, 3};
     double[] weights = {1, -1, 1};
     v.evaluate(values, weights, 0, 3);
 }

 @Test(expected = NotPositiveException.class)
 public void testZeroWeightSumThrows() {
     Variance v = new Variance();
     double[] values = {1, 2, 3};
     double[] weights = {0, 0, 0};
     v.evaluate(values, weights, 0, 3);
 }

 @Test
 public void testUnweightedSegmentWithGivenMean() {
     Variance v = new Variance(true);
     double[] values = {10, 11, 12, 13, 14};
     double mean = 12.0;
     double result = v.evaluate(values, mean, 1, 3);
     assertEquals(1.0, result, EPS);
 }

 @Test
 public void testWeightedSegmentLargeConsistency() {
     Variance v = new Variance(true);
     double[] values = new double[100];
     double[] weights = new double[100];
     for (int i = 0; i < 100; i++) {
         values[i] = i;
         weights[i] = (i % 10) + 1.0;
     }
     int begin = 20;
     int length = 50;
     double segmentResult = v.evaluate(values, weights, begin, length);
     double[] subVals = new double[length];
     double[] subWts = new double[length];
     System.arraycopy(values, begin, subVals, 0, length);
     System.arraycopy(weights, begin, subWts, 0, length);
     double subResult = v.evaluate(subVals, subWts);
     assertEquals(subResult, segmentResult, EPS);
 }

}
