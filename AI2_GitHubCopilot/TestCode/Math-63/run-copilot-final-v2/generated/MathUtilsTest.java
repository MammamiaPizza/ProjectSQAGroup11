package org.apache.commons.math.util;

import static org.junit.Assert.*;
import org.junit.Test;

public class MathUtilsTest {

 /** Two empty arrays are equal for both equals and equalsIncludingNaN */
 @Test
 public void testEqualsEmptyArrays() {
     double[] a = {};
     double[] b = {};
     assertTrue(MathUtils.equals(a, b));
     assertTrue(MathUtils.equalsIncludingNaN(a, b));
 }

 /** Identical non-NaN arrays should be equal */
 @Test
 public void testEqualsIdenticalNormalArrays() {
     double[] a = {1.0, 2.0, 3.0};
     double[] b = {1.0, 2.0, 3.0};
     assertTrue(MathUtils.equals(a, b));
     assertTrue(MathUtils.equalsIncludingNaN(a, b));
 }

 /** Arrays with a single differing element are not equal */
 @Test
 public void testEqualsDifferentElement() {
     double[] a = {1.0, 2.0, 3.0};
     double[] b = {1.0, 2.5, 3.0};
     assertFalse(MathUtils.equals(a, b));
     assertFalse(MathUtils.equalsIncludingNaN(a, b));
 }

 /** Arrays containing NaN at the same index: equals must be false, equalsIncludingNaN must be true
*/
 @Test
 public void testEqualsNaNArrays() {
     double[] a = {1.0, Double.NaN, 3.0};
     double[] b = {1.0, Double.NaN, 3.0};
     assertFalse(MathUtils.equals(a, b));
     assertTrue(MathUtils.equalsIncludingNaN(a, b));
 }

 /** NaN present in only one array → both methods return false */
 @Test
 public void testEqualsNaNInOneArray() {
     double[] a = {1.0, Double.NaN, 3.0};
     double[] b = {1.0, 2.0, 3.0};
     assertFalse(MathUtils.equals(a, b));
     assertFalse(MathUtils.equalsIncludingNaN(a, b));
 }

 /** Arrays containing only NaN */
 @Test
 public void testEqualsAllNaNArrays() {
     double[] a = {Double.NaN, Double.NaN};
     double[] b = {Double.NaN, Double.NaN};
     assertFalse(MathUtils.equals(a, b));
     assertTrue(MathUtils.equalsIncludingNaN(a, b));
 }

 /** Different lengths → false */
 @Test
 public void testEqualsDifferentLengths() {
     double[] a = {1.0};
     double[] b = {1.0, 2.0};
     assertFalse(MathUtils.equals(a, b));
     assertFalse(MathUtils.equalsIncludingNaN(a, b));
 }

 /** Null first argument should throw NullPointerException for both methods */
 @Test(expected = NullPointerException.class)
 public void testEqualsNullFirstArgEquals() {
     MathUtils.equals(null, new double[]{1.0});
 }

 /** Null second argument should throw NullPointerException for equals */
 @Test(expected = NullPointerException.class)
 public void testEqualsNullSecondArgEquals() {
     MathUtils.equals(new double[]{1.0}, null);
 }

 /** Null first argument should throw NullPointerException for equalsIncludingNaN */
 @Test(expected = NullPointerException.class)
 public void testEqualsIncludingNaNNullFirstArg() {
     MathUtils.equalsIncludingNaN(null, new double[]{1.0});
 }

 /** Null second argument should throw NullPointerException for equalsIncludingNaN */
 @Test(expected = NullPointerException.class)
 public void testEqualsIncludingNaNNullSecondArg() {
     MathUtils.equalsIncludingNaN(new double[]{1.0}, null);
 }

 /** +0.0 and -0.0 are considered equal according to ULP-based equality */
 @Test
 public void testEqualsSignedZero() {
     double[] a = {+0.0};
     double[] b = {-0.0};
     assertTrue(MathUtils.equals(a, b));
     assertTrue(MathUtils.equalsIncludingNaN(a, b));
 }

 /** Infinity handling: +Inf == +Inf, -Inf == -Inf, +Inf != -Inf */
 @Test
 public void testEqualsInfinity() {
     double[] a = {Double.POSITIVE_INFINITY};
     double[] b = {Double.POSITIVE_INFINITY};
     assertTrue(MathUtils.equals(a, b));
     assertTrue(MathUtils.equalsIncludingNaN(a, b));

     a = new double[]{Double.NEGATIVE_INFINITY};
     b = new double[]{Double.NEGATIVE_INFINITY};
     assertTrue(MathUtils.equals(a, b));
     assertTrue(MathUtils.equalsIncludingNaN(a, b));

     a = new double[]{Double.POSITIVE_INFINITY};
     b = new double[]{Double.NEGATIVE_INFINITY};
     assertFalse(MathUtils.equals(a, b));
     assertFalse(MathUtils.equalsIncludingNaN(a, b));
 }

}
