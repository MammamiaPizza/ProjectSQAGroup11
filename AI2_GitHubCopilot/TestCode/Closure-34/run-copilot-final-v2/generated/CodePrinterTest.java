package com.google.javascript.jscomp;

 import static org.junit.Assert.assertEquals;

 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;

 import org.junit.Test;

 public class CodePrinterTest {

   private Node createNumber(int value) {
     return Node.newNumber(value);
   }

   private Node buildAddChain(int depth) {
     if (depth <= 1) {
       return createNumber(depth);
     }
     Node current = createNumber(1);
     for (int i = 2; i <= depth; i++) {
       current = new Node(Token.ADD, current, createNumber(i));
     }
     return current;
   }

   private String expectedAddOutput(int depth) {
     StringBuilder sb = new StringBuilder();
     for (int i = 1; i <= depth; i++) {
       if (i > 1) sb.append(" + ");
       sb.append(i);
     }
     return sb.toString();
   }

   private String printNode(Node root) {
     CodePrinter.Builder builder = new CodePrinter.Builder(root);
     builder.setPrettyPrint(true);
     builder.setLineBreak(false);
     return builder.build();
   }

   @Test
   public void testAddDepth0() {
     Node root = createNumber(1);
     String output = printNode(root);
     assertEquals("1", output.trim());
   }

   @Test
   public void testAddDepth10() {
     Node root = buildAddChain(10);
     String output = printNode(root);
     assertEquals(expectedAddOutput(10), output.trim());
   }

   @Test
   public void testAddDepth50() {
     Node root = buildAddChain(50);
     String output = printNode(root);
     assertEquals(expectedAddOutput(50), output.trim());
   }

   @Test
   public void testAddDepth100() {
     Node root = buildAddChain(100);
     String output = printNode(root);
     assertEquals(expectedAddOutput(100), output.trim());
   }

   @Test
   public void testAddDepth500() {
     Node root = buildAddChain(500);
     String output = printNode(root);
     assertEquals(expectedAddOutput(500), output.trim());
   }

   @Test
   public void testAddDepth1000() {
     Node root = buildAddChain(1000);
     String output = printNode(root);
     assertEquals(expectedAddOutput(1000), output.trim());
   }

   private Node buildChain(int op, int count) {
     Node current = createNumber(1);
     for (int i = 2; i <= count; i++) {
       current = new Node(op, current, createNumber(i));
     }
     return current;
   }

   private String expectedOpOutput(int op, int count) {
     String opStr;
     switch (op) {
       case Token.SUB: opStr = " - "; break;
       case Token.MUL: opStr = " * "; break;
       case Token.BITOR: opStr = " | "; break;
       default: opStr = " ? ";
     }
     StringBuilder sb = new StringBuilder();
     for (int i = 1; i <= count; i++) {
       if (i > 1) sb.append(opStr);
       sb.append(i);
     }
     return sb.toString();
   }

   @Test
   public void testSubDepth100() {
     Node root = buildChain(Token.SUB, 100);
     String output = printNode(root);
     assertEquals(expectedOpOutput(Token.SUB, 100), output.trim());
   }

   @Test
   public void testMulDepth100() {
     Node root = buildChain(Token.MUL, 100);
     String output = printNode(root);
     assertEquals(expectedOpOutput(Token.MUL, 100), output.trim());
   }

   @Test
   public void testBitorDepth100() {
     Node root = buildChain(Token.BITOR, 100);
     String output = printNode(root);
     assertEquals(expectedOpOutput(Token.BITOR, 100), output.trim());
   }

   @Test
   public void testMixedPrecedence() {
     Node add = new Node(Token.ADD, createNumber(1), createNumber(2));
     Node mul = new Node(Token.MUL, add, createNumber(3));
     String output = printNode(mul);
     assertEquals("(1 + 2) * 3", output.trim());
   }

   @Test
   public void testNestedParenthesized() {
     Node add1 = new Node(Token.ADD, createNumber(1), createNumber(2));
     Node add2 = new Node(Token.ADD, createNumber(3), createNumber(4));
     Node root = new Node(Token.ADD, add1, add2);
     String output = printNode(root);
     assertEquals("1 + 2 + 3 + 4", output.trim());
   }

   @Test
   public void testMixedPrecedenceDeep() {
     Node root = null;
     for (int i = 1; i <= 20; i += 2) {
       Node mul = new Node(Token.MUL, createNumber(i), createNumber(i + 1));
       if (root == null) {
         root = mul;
       } else {
         root = new Node(Token.ADD, root, mul);
       }
     }
     String output = printNode(root);
     StringBuilder expected = new StringBuilder();
     for (int i = 1; i <= 20; i += 2) {
       if (i > 1) expected.append(" + ");
       expected.append(i).append(" * ").append(i + 1);
     }
     assertEquals(expected.toString(), output.trim());
   }
 }
