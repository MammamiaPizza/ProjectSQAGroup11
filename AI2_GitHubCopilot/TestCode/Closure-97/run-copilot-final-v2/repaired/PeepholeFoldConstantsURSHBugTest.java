package com.google.javascript.jscomp;

 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertEquals;
 import java.lang.reflect.Method;
 import org.junit.Test;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;

 public class PeepholeFoldConstantsURSHBugTest {

     private static final PeepholeFoldConstants folder = new PeepholeFoldConstants();
     private static final Method tryFoldShiftMethod;
     private static final Method tryFoldBitAndOrMethod;

     static {
         try {
             tryFoldShiftMethod = PeepholeFoldConstants.class.getDeclaredMethod(
                     "tryFoldShift", Node.class, Node.class, Node.class);
             tryFoldShiftMethod.setAccessible(true);
             tryFoldBitAndOrMethod = PeepholeFoldConstants.class.getDeclaredMethod(
                     "tryFoldBitAndOr", Node.class, Node.class, Node.class);
             tryFoldBitAndOrMethod.setAccessible(true);
         } catch (NoSuchMethodException e) {
             throw new RuntimeException(e);
         }
     }

     private Node invokeFoldShift(int tokenType, double leftVal, double rightVal) throws Exception {
         Node n = new Node(tokenType);
         Node left = Node.newNumber(leftVal);
         Node right = Node.newNumber(rightVal);
         n.addChildToBack(left);
         n.addChildToBack(right);
         Node parent = new Node(Token.SCRIPT);
         parent.addChildToBack(n);
         return (Node) tryFoldShiftMethod.invoke(folder, n, left, right);
     }

     private Node invokeFoldBitAndOr(int tokenType, double leftVal, double rightVal) throws
Exception {
         Node n = new Node(tokenType);
         Node left = Node.newNumber(leftVal);
         Node right = Node.newNumber(rightVal);
         n.addChildToBack(left);
         n.addChildToBack(right);
         Node parent = new Node(Token.SCRIPT);
         parent.addChildToBack(n);
         return (Node) tryFoldBitAndOrMethod.invoke(folder, n, left, right);
     }

     // ------------------- URSH folding tests -----------------

     @Test
     public void testNegativeOneURSHZero() throws Exception {
         // JS: -1>>>0 == 4294967295
         Node result = invokeFoldShift(Token.URSH, -1.0, 0.0);
         assertTrue(result.isNumber());
         assertEquals(4294967295.0, result.getDouble(), 0.0);
     }

     @Test
     public void testNegativeOneURSHOne() throws Exception {
         // JS: -1>>>1 == 2147483647
         Node result = invokeFoldShift(Token.URSH, -1.0, 1.0);
         assertTrue(result.isNumber());
         assertEquals(2147483647.0, result.getDouble(), 0.0);
     }

     @Test
     public void testIntegerMinValueURSHZero() throws Exception {
         // JS: (-2147483648)>>>0 == 2147483648 (unsigned interpretation)
         Node result = invokeFoldShift(Token.URSH, (double) Integer.MIN_VALUE, 0.0);
         assertTrue(result.isNumber());
         assertEquals(2147483648.0, result.getDouble(), 0.0);
     }

     @Test
     public void testIntegerMinValueURSHOne() throws Exception {
         // JS: Int.MIN_VALUE>>>1 == 1073741824
         Node result = invokeFoldShift(Token.URSH, (double) Integer.MIN_VALUE, 1.0);
         assertTrue(result.isNumber());
         assertEquals(1073741824.0, result.getDouble(), 0.0);
     }

     @Test
     public void testNegativeOneURSH31() throws Exception {
         // JS: -1>>>31 == 1
         Node result = invokeFoldShift(Token.URSH, -1.0, 31.0);
         assertTrue(result.isNumber());
         assertEquals(1.0, result.getDouble(), 0.0);
     }

     @Test
     public void testPositiveLeftURSH() throws Exception {
         // JS: 100>>>3 == 12
         Node result = invokeFoldShift(Token.URSH, 100.0, 3.0);
         assertTrue(result.isNumber());
         assertEquals(12.0, result.getDouble(), 0.0);
     }

     @Test
     public void testZeroURSHShift() throws Exception {
         // JS: 0>>>5 == 0
         Node result = invokeFoldShift(Token.URSH, 0.0, 5.0);
         assertTrue(result.isNumber());
         assertEquals(0.0, result.getDouble(), 0.0);
     }

     @Test
     public void testURSHShiftZero() throws Exception {
         // JS: -5>>>0 == 4294967291
         Node result = invokeFoldShift(Token.URSH, -5.0, 0.0);
         assertTrue(result.isNumber());
         assertEquals(4294967291.0, result.getDouble(), 0.0);
     }

     // ------------------- BITAND/BITOR folding tests -----------------

     @Test
     public void testBitAndPositivePositive() throws Exception {
         // 5&3 == 1
         Node result = invokeFoldBitAndOr(Token.BITAND, 5.0, 3.0);
         assertTrue(result.isNumber());
         assertEquals(1.0, result.getDouble(), 0.0);
     }

     @Test
     public void testBitAndNegativePositive() throws Exception {
         // -5&3 == 3 (two's complement)
         Node result = invokeFoldBitAndOr(Token.BITAND, -5.0, 3.0);
         assertTrue(result.isNumber());
         assertEquals(3.0, result.getDouble(), 0.0);
     }

     @Test
     public void testBitOrNegativePositive() throws Exception {
         // -8|7 == -1
         Node result = invokeFoldBitAndOr(Token.BITOR, -8.0, 7.0);
         assertTrue(result.isNumber());
         assertEquals(-1.0, result.getDouble(), 0.0);
     }

     @Test
     public void testBitAndNoFoldNonInteger() throws Exception {
         // Non-integer operand should prevent folding
         Node result = invokeFoldBitAndOr(Token.BITAND, 3.5, 2.0);
         // Since left is fractional, folding should not happen; result should be the original node
(of type BITAND)
         assertSame(Token.BITAND, result.getType());
     }
 }
