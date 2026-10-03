package com.google.gson.functional;

import static org.junit.Assert.*;

import org.junit.Test;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class PrimitiveTest {

 private final Gson gson = new Gson();

 @Test
 public void testIntegerFromString() {
     int result = gson.fromJson("\"123\"", int.class);
     assertEquals(123, result);
 }

 @Test
 public void testShortFromString() {
     short result = gson.fromJson("\"100\"", short.class);
     assertEquals((short) 100, result);
 }

 @Test
 public void testByteFromString() {
     byte result = gson.fromJson("\"42\"", byte.class);
     assertEquals((byte) 42, result);
 }

 @Test
 public void testLongFromString() {
     long result = gson.fromJson("\"9999999999\"", long.class);
     assertEquals(9999999999L, result);
 }

 @Test
 public void testFloatFromString() {
     float result = gson.fromJson("\"3.14\"", float.class);
     assertEquals(3.14f, result, 0.001f);
 }

 @Test
 public void testDoubleFromString() {
     double result = gson.fromJson("\"2.718\"", double.class);
     assertEquals(2.718, result, 0.001);
 }

 @Test
 public void testDoubleScientificNotationFromString() {
     double result = gson.fromJson("\"1e2\"", double.class);
     assertEquals(100.0, result, 0.001);
 }

 @Test
 public void testNegativeIntegerFromString() {
     int result = gson.fromJson("\"-45\"", int.class);
     assertEquals(-45, result);
 }

 @Test
 public void testIntegerFromNumericToken() {
     int result = gson.fromJson("123", int.class);
     assertEquals(123, result);
 }

 @Test(expected = JsonSyntaxException.class)
 public void testNonNumericStringThrowsException() {
     gson.fromJson("\"abc\"", int.class);
 }

 @Test(expected = JsonSyntaxException.class)
 public void testEmptyStringThrowsException() {
     gson.fromJson("\"\"", int.class);
 }

 @Test
 public void testNullReturnsNullForBoxedInteger() {
     Integer result = gson.fromJson("null", Integer.class);
     assertNull(result);
 }

}