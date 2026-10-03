package org.apache.commons.jxpath.ri.compiler;

import junit.framework.TestCase;

public class CoreOperationCompareNanTest extends TestCase {

 private Constant constant(Object value) {
     return new Constant(value);
 }

 private Object eq(Object l, Object r) {
     return new CoreOperationEqual(constant(l), constant(r)).computeValue(null);
 }

 private Object neq(Object l, Object r) {
     return new CoreOperationNotEqual(constant(l), constant(r)).computeValue(null);
 }

 private boolean eqObjects(Object l, Object r) {
     return new CoreOperationEqual(null, null).equal(l, r);
 }

 public void testNanEqualsNanIsFalse() {
     assertEquals(Boolean.FALSE, eq(new Double(Double.NaN), new Double(Double.NaN)));
 }

 public void testNanNotEqualsNanIsTrue() {
     assertEquals(Boolean.TRUE, neq(new Double(Double.NaN), new Double(Double.NaN)));
 }

 public void testNaNEqualsNumberIsFalse() {
     assertEquals(Boolean.FALSE, eq(new Double(Double.NaN), new Double(1.0)));
     assertEquals(Boolean.FALSE, eq(new Double(1.0), new Double(Double.NaN)));
 }

 public void testNaNNotEqualsNumberIsTrue() {
     assertEquals(Boolean.TRUE, neq(new Double(Double.NaN), new Double(1.0)));
     assertEquals(Boolean.TRUE, neq(new Double(1.0), new Double(Double.NaN)));
 }

 public void testEqualNumbers() {
     assertEquals(Boolean.TRUE, eq(new Double(42.0), new Double(42.0)));
 }

 public void testDifferentNumbers() {
     assertEquals(Boolean.FALSE, eq(new Double(42.0), new Double(43.0)));
     assertEquals(Boolean.TRUE, neq(new Double(42.0), new Double(43.0)));
 }

 public void testEqualStrings() {
     assertEquals(Boolean.TRUE, eq("apple", "apple"));
 }

 public void testDifferentStrings() {
     assertEquals(Boolean.FALSE, eq("apple", "orange"));
     assertEquals(Boolean.TRUE, neq("apple", "orange"));
 }

 public void testBooleanComparison() {