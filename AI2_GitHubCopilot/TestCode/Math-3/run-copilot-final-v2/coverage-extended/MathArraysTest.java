package org.apache.commons.math3.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class MathArraysTest {

 @Test
 public void testLinearCombinationWithSingleElementArray() {
     double[] a = { 2.0 };
     double[] b = { 3.0 };
     assertEquals(6.0, MathArrays.linearCombination(a, b), 0.0);
 }

 @Test
 public void testLinearCombinationSingleElementZero() {
     double[] a = { 0.0 };
     double[] b = { 7.0 };
     assertEquals(0.0, MathArrays.linearCombination(a, b), 0.0);
 }

 @Test
 public void testLinearCombinationSingleElementNegative() {
     double[] a = { -4.0 };
     double[] b = { 5.0 };
     assertEquals(-20.0, MathArrays.linearCombination(a, b), 0.0);
 }

 @Test
 public void testLinearCombinationSingleElementFractionalPositive() {
     double[] a = { 0.5 };
     double[] b = { 0.25 };
     assertEquals(0.125, MathArrays.linearCombination(a, b), 1e-15);
 }

 @Test
 public void testLinearCombinationSingleElementFractionalNegative() {
     double[] a = { -0.5 };
     double[] b = { -0.25 };
     assertEquals(0.125, MathArrays.linearCombination(a, b), 1e-15);
 }

 @Test
 public void testLinearCombinationTwoElements() {
     double[] a = { 1.0, 2.0 };
     double[] b = { 3.0, 4.0 };
     assertEquals(11.0, MathArrays.linearCombination(a, b), 1e-15);
 }

 @Test
 public void testLinearCombinationThreeElements() {
     double[] a = { 1.0, 2.0, 3.0 };
     double[] b = { 4.0, 5.0, 6.0 };
     assertEquals(32.0, MathArrays.linearCombination(a, b), 1e-15);
 }

 @Test
 public void testLinearCombinationFourElements() {
     double[] a = { 1.0, 2.0, 3.0, 4.0 };
     double[] b = { 5.0, 6.0, 7.0, 8.0 };
     assertEquals(70.0, MathArrays.linearCombination(a, b), 1e-15);
 }

 @Test
 public void testLinearCombinationTwoElementsIncludingZero() {
     double[] a = { 0.0, 5.0 };
     double[] b = { 3.0, 0.0 };
     assertEquals(0.0, MathArrays.linearCombination(a, b), 0.0);
 }

 @Test
 public void testLinearCombinationTwoElementsMixedSigns() {
     double[] a = { -1.0, 2.0 };
     double[] b = { 3.0, -4.0 };
     assertEquals(-11.0, MathArrays.linearCombination(a, b), 1e-15);
 }

}
