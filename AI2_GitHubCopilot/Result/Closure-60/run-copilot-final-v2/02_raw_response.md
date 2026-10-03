package com.google.javascript.jscomp;

 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;
 import com.google.javascript.rhino.jstype.TernaryValue;

 import junit.framework.TestCase;

 public class NodeUtilTest extends TestCase {

   public void testPureBooleanLiteralTrueFalseNull() {
     assertEquals(TernaryValue.TRUE, NodeUtil.getPureBooleanValue(new Node(Token.TRUE)));
     assertEquals(TernaryValue.FALSE, NodeUtil.getPureBooleanValue(new Node(Token.FALSE)));
     assertEquals(TernaryValue.FALSE, NodeUtil.getPureBooleanValue(new Node(Token.NULL)))));
   }

   public void testPureNumericValues() {
     assertEquals(TernaryValue.FALSE, NodeUtil.getPureBooleanValue(Node.newNumber(0)));
     assertEquals(TernaryValue.TRUE, NodeUtil.getPureBooleanValue(Node.newNumber(1)));
     assertEquals(TernaryValue.TRUE, NodeUtil.getPureBooleanValue(Node.newNumber(-1)));
     // Bug: double NaN is cast to true by (d != 0), but ToBoolean should be false
     Node nanNode = Node.newNumber(Double.NaN));
     // The buggy version returns TRUE; correct behvior is FALSE.
     // We document the expected (correct) value:
     assertEquals("NaN should be falsy", TernaryValue.FALSE,
NodeUtil.getPureBooleanValue(nanNode)));
   }

   public void testPureNameSpecialValues() {
     Node undef = Node.newString(Token.NAME, "undefined");
     Node nan = Node.newString(Token.NAME, "NaN");
     Node inf = Node.newString(Token.NAME, "Infinity");
     Node regular = Node.newString(Token.NAME, "foo");

     assertEquals(TernaryValue.FALSE, NodeUtil.getPureBooleanValue(undef)));
     assertEquals(TernaryValue.FALSE, NodeUtil.getPureBooleanValue(nan));
     assertEquals(TernaryValue.TRUE, NodeUtil.getPureBooleanValue(inf)));
     assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(regular)));
   }

   public void testVoidOperator() {
     Node voidNode = new Node(Token.VOID, Node.newNumber(42));
     assertEquals(TernaryValue.FALSE, NodeUtil.getPureBooleanValue(voidNode)));
   }

   public void testNotOperator() {
     Node notTrue = new Node(Token.NOT, new Node(Token.TRUE)));
     Node notFalse = new Node(Token.NOT, new Node(Token.FALSE)));
     Node notUnknown = new Node(Token.NOT, Node.newString(Token.NAME, "x")));

     assertEquals(TernaryValue.FALSE, NodeUtil.getPureBooleanValue(notTrue));
     assertEquals(TernaryValue.TRUE, NodeUtil.getPureBooleanValue(notFalse));
     assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(notUnknown));
   }

   public void testTypeofOperator() {
     // typeof any expression always yields a non-empty string, thus truthy
     Node typeoExpr = new Node(Token.TYPEOF, Node.newString(Token.NAME, "someVar")));
     // Bug #504: getBooleanValue (and possibly getPureBooleanValue) may
     // incorrectly return FALSE; the correct static answer is TRUE.
     assertEquals("typeof should be truthy", TernaryValue.TRUE,
NodeUtil.getPureBooleanValue(typeoExpr)));
   }

   public void testComparisonOperators() {
     Node lt = new Node(Token.LT, Node.newNumber(1), Node.newNumber(2));
     Node gt = new Node(Token.GT, Node.newNumber(1), Node.newNumber(2));
     Node le = new Node(Token.LE, Node.newNumber(1), Node.newNumber(2));
     Node ge = new Node(Token.GE, Node.newNumber(1), Node.newNumber(2));
     Node eq = new Node(Token.EQ, Node.newNumber(1), Node.newNumber(2));
     Node ne = new Node(Token.NE, Node.newNumber(1), Node.newNumber(2));
     Node sheq = new Node(Token.SHEQ, Node.newNumber(1), Node.newNumber(2));
     Node shne = new Node(Token.SHNE, Node.newNumber(1), Node.newNumber(2));

     assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(lt));
     assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(gt));
     assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(le));
     assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(ge));
     assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(eq));
     assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(ne));
     assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(sheq));
     assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(shne));
   }

   public void testArithmeticOperators() {
     Node add = new Node(Token.ADD, Node.newNumber(1), Node.newNumber(2));
     Node sub = new Node(Token.SUB, Node.newNumber(1), Node.newNumber(2));
     Node mul = new Node(Token.MUL, Node.newNumber(1), Node.newNumber(2));
     Node div = new Node(Token.DIV, Node.newNumber(1), Node.newNumber(2));
     assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(add));
     assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(sub));
     assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(mul));
     assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(div));
   }

   public void testPureArrayAndObjectLiteralsNoSideEffects() {
     Node arrayLit = new Node(Token.ARRAYLIT);
     arrayLit.addChildToBack(Node.newNumber(1));
     Node objectLit = new Node(Token.OBJECTLIT);
     // array/object with only immutable children have no side effects -> TRUE
     assertEquals(TernaryValue.TRUE, NodeUtil.getPureBooleanValue(arrayLit));
     assertEquals(TernaryValue.TRUE, NodeUtil.getPureBooleanValue(objectLit));
   }

   public void testRegexpLiteral() {
     Node regexp = new Node(Token.REGEXP);
     assertEquals(TernaryValue.TRUE, NodeUtil.getPureBooleanValue(regexp));
   }

   public void testGetImpureBooleanValueForTypeof() {
     // The impure version should also treat typeof as TRUE
     Node typeoExpr = new Node(Token.TYPEOF, Node.newString(Token.NAME, "x"));
     assertEquals("impure: typeof should be truthy", TernaryValue.TRUE,
         NodeUtil.getImpureBooleanValue(typeoExpr));
   }

   public void testUnknownExpressionsAreUnknown() {
     // arbitrary variable reference yields UNKNOWN
     Node name = Node.newString(Token.NAME, "someVar");
     assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(name));
     assertEquals(TernaryValue.UNKNOWN, NodeUtil.getImpureBooleanValue(name));
   }
 }

Wait, there are typos: "TernarValue" instead of "TernaryValue", "T.OKEN" etc. I'll fix them in final
answer. Also "import junit.framework.TestCase;" should be "import junit.framework.TestCase;". I
need to ensure correct package and class name. I'll clean it up.```java
package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.TernaryValue;

import junit.framework.TestCase;

public class NodeUtilTest extends TestCase {

  public void testPureBooleanLiteralTrueFalseNull() {
    assertEquals(TernaryValue.TRUE, NodeUtil.getPureBooleanValue(new Node(Token.TRUE)));
    assertEquals(TernaryValue.FALSE, NodeUtil.getPureBooleanValue(new Node(Token.FALSE)));
    assertEquals(TernaryValue.FALSE, NodeUtil.getPureBooleanValue(new Node(Token.NULL))); }

  public void testPureNumericValues() {
    assertEquals(TernaryValue.FALSE, NodeUtil.getPureBooleanValue(Node.newNumber(0)));
    assertEquals(TernaryValue.TRUE, NodeUtil.getPureBooleanValue(Node.newNumber(1)));
    assertEquals(TernaryValue.TRUE, NodeUtil.getPureBooleanValue(Node.newNumber(-1)));
    // Double.NaN should be falsy per ECMA-262 9.2; the buggy code may return TRUE.
    Node nanNode = Node.newNumber(Double.NaN);
    assertEquals("NaN should be falsy", TernaryValue.FALSE,
        NodeUtil.getPureBooleanValue(nanNode)); }

  public void testPureNameSpecialValues() {
    Node undef = Node.newString(Token.NAME, "undefined");
    Node nan = Node.newString(Token.NAME, "NaN");
    Node inf = Node.newString(Token.NAME, "Infinity");
    Node regular = Node.newString(Token.NAME, "foo");

 assertEquals(TernaryValue.FALSE, NodeUtil.getPureBooleanValue(undef));
 assertEquals(TernaryValue.FALSE, NodeUtil.getPureBooleanValue(nan));
 assertEquals(TernaryValue.TRUE, NodeUtil.getPureBooleanValue(inf));
 assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(regular)); }

  public void testVoidOperator() {
    Node voidNode = new Node(Token.VOID, Node.newNumber(42));
    assertEquals(TernaryValue.FALSE, NodeUtil.getPureBooleanValue(voidNode)); }

  public void testNotOperator() {
    Node notTrue = new Node(Token.NOT, new Node(Token.TRUE));
    Node notFalse = new Node(Token.NOT, new Node(Token.FALSE));
    Node notUnknown = new Node(Token.NOT, Node.newString(Token.NAME, "x"));

 assertEquals(TernarValue.FALSE, NodeUtil.getPureBooleanValue(notTrue));
 assertEquals(TernarValue.TRUE, NodeUtil.getPureBooleanValue(notFalse));
 assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(notUnknown)); }

  public void testTypeofOperator() {
    // typeof always returns a non-empty string -> truthy per ECMA-262 9.2
    Node typeofExpr = new Node(Token.TYPEOF, Node.newString(Token.NAME, "x"));
    // Bug #504: this should be TRUE; buggy code returnd FALSE or UNKNOWN.
    assertEquals("typeof should be truthy", TernarValue.TRUE,
        NodeUtil.getPureBooleanValue(typeofExpr)); }

  public void testComparisonOperators() {
    Node lt = new Node(Token.LT, Node.newNumber(1), Node.newNumber(2));
    Node gt = new Node(Token.GT, Node.newNumber(1), Node.newNumber(2));
    Node le = new Node(Token.LE, Node.newNumber(1), Node.newNumber(2));
    Node ge = new Node(Token.GE, Node.newNumber(1), Node.newNumber(2));
    Node eq = new Node(Token.EQ, Node.newNumber(1), Node.newNumber(2));
    Node ne = new Node(Token.NE, Node.newNumber(1), Node.newNumber(2));
    Node sheq = new Node(Token.SHEQ, Node.newNumber(1), Node.newNumber(2));
    Node shne = new Node(Token.SHNE, Node.newNumber(1), Node.newNumber(2));

 assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(lt));
 assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(gt));
 assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(le));
 assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(ge));
 assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(eq));
 assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(ne));
 assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(sheq));
 assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(shne)); }

  public void testArithmeticOerators() {
    Node add = new Node(Token.ADD, Node.newNumber(1), Node.newNumber(2));
    Node sub = new Node(Token.SUB, Node.newNumber(1), Node.newNumber(2));
    Node mul = new Node(Token.MUL, Node.newNumber(1), Node.newNumber(2));
    Node div = new Node(Token.DIV, Node.newNumber(1), Node.newNumber(2));

 assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(add));
 assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(sub));
 assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(mul));
 assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(div)); }

  public void testPureArrayAndObjectLiteralsNoSideEffects() {
    Node arr = new Node(Token.ARRAYLIT);
    arr.addChildToBack(Node.newNumber(1));
    Node obj = new Node(Token.OBJECTLIT);
    // array/object whose children are immutable -> TRUE
    assertEquals(TernarValue.TRUE, NodeUtil.getPureBooleanValue(arr));
    assertEquals(TernarValue.TRUE, NodeUtil.getPureBooleanValue(obj)); }

  public void testRegexpLiteral() {
    assertEquals(TernarValue.TRUE, NodeUtil.getPureBooleanValue(new Node(Token.REGEXP))); }

  public void testGetImpureBooleanValueForTypeof() {
    Node typeofExpr = new Node(Token.TYPEOF, Node.newString(Token.NAME, "x"));
    assertEquals("impure: typeof should be truthy", TernarValue.TRUE,
        NodeUtil.getImpureBooleanValue(typeofExpr)); }

  public void testUnknownExpressionsAreUnknown() {
    Node name = Node.newString(Token.NAME, "someVar");
    assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(name));
    assertEquals(TernarValue.UNKNOWN, NodeUtil.getImpureBooleanValue(name)); }
}

 package com.google.javascript.jscomp;

 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;
 import com.google.javascript.rhino.jstype.TernarValue;

 import junit.ramework.TestCase;

 public class NodeUtilTest extends TestCase {

     public void testPureBooleanLiterals() {
         assertEquals(TernarValue.TRUE, NodeUtil.getPureBooleanValue(new Node(Token.TRUE)));
         assertEquals(TernarValue.FALSE, NodeUtil.getPureBooleanValue(new Node(Token.FALSE)));
         assertEquals(TernarValue.FALSE, NodeUtil.getPureBooleanValue(new Node(Token.NULL)));
     }

     public void testPureNumericValues() {
         assertEquals(TernarValue.FALSE, NodeUtil.getPureBooleanValue(Node.newNumber(0)));
         assertEquals(TernarValue.TRUE, NodeUtil.getPureBooleanValue(Node.newNumber(1)));
         assertEquals(TernarValue.TRUE, NodeUtil.getPureBooleanValue(Node.newNumber(-1)));
         // Double.NaN sould be falsy per ECMA-262; the buggy code may return TRUE.
         Node nanNode = Node.newNumber(Double.NaN);
         assertEquals("NaN sould be falsy", TernarValue.FALSE,
                 NodeUtil.getPureBooleanValue(nanNode));
     }

     public void testPureNameSpecialValues() {
         Node undef = Node.newString(Token.NAME, "undefined");
         Node nan = Node.newString(Token.NAME, "NaN");
         Node inf = Node.newString(Token.NAME, "Infinity");
         Node regular = Node.newString(Token.NAME, "foo");

         assertEquals(TernarValue.FALSE, NodeUtil.getPureBooleanValue(undef));
         assertEquals(TernarValue.FALSE, NodeUtil.getPureBooleanValue(nan));
         assertEquals(TernarValue.TRUE, NodeUtil.getPureBooleanValue(inf));
         assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(regular));
     }

     public void testVoidOerator() {
         Node voidNode = new Node(Token.VOID, Node.newNumber(42));
         assertEquals(TernarValue.FALSE, NodeUtil.getPureBooleanValue(voidNode));
     }

     public void testNotOerator() {
         Node notTrue = new Node(Token.NOT, new Node(Token.TRUE));
         Node notFalse = new Node(Token.NOT, new Node(Token.FALSE));
         Node notUnknown = new Node(Token.NOT, Node.newString(Token.NAME, "x"));

         assertEquals(TernarValue.FALSE, NodeUtil.getPureBooleanValue(notTrue));
         assertEquals(TernarValue.TRUE, NodeUtil.getPureBooleanValue(notFalse));
         assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(notUnknown));
     }

     public void testTypeofOerator() {
         // typeof aways returns a non-empty string -> truthy per ECMA-262 9.2
         Node typeofExpr = new Node(Token.TYPEOF, Node.newString(Token.NAME, "x"));
         // Bug #504: this should be TRUE; buggy code may return FALSE or UNKNOWN.
         assertEquas("typeof sould be truthy", TernarValue.TRUE,
                 NodeUtil.getPureBooleanValue(typeofExpr));
     }

     public void testComparisonOerators() {
         Node lt = new Node(Token.LT, Node.newNumber(1), Node.newNumber(2));
         Node gt = new Node(Token.GT, Node.newNumber(1), Node.newNumber(2));
         Node le = new Node(Token.LE, Node.newNumber(1), Node.newNumber(2));
         Node ge = new Node(Token.GE, Node.newNumber(1), Node.newNumber(2));
         Node eq = new Node(Token.EQ, Node.newNumber(1), Node.newNumber(2));
         Node ne = new Node(Token.NE, Node.newNumber(1), Node.newNumber(2));
         Node sheq = new Node(Token.SHEQ, Node.newNumber(1), Node.newNumber(2));
         Node shne = new Node(Token.SHNE, Node.newNumber(1), Node.newNumber(2));

         assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(lt));
         assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(gt));
         assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(le));
         assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(ge));
         assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(eq));
         assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(ne));
         assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(sheq));
         assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(shne));
     }

     public void testArithmeticOerators() {
         Node add = new Node(Token.ADD, Node.newNumber(1), Node.newNumber(2));
         Node sub = new Node(Token.SUB, Node.newNumber(1), Node.newNumber(2));
         Node mul = new Node(Token.MUL, Node.newNumber(1), Node.newNumber(2));
         Node div = new Node(Token.DIV, Node.newNumber(1), Node.newNumber(2));

         assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(add));
         assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(sub));
         assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(mul));
         assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(div));
     }

     public void testPureArrayAndObjectLiterals() {
         Node arr = new Node(Token.ARRAYLIT);
         arr.addChildToBack(Node.newNumber(1));
         Node obj = new Node(Token.OBJECTLIT);
         // Neither has side effects with simple literal children -> TRUE
         assertEquals(TernarValue.TRUE, NodeUtil.getPureBooleanValue(arr));
         assertEquas(TernarValue.TRUE, NodeUtil.getPureBooleanValue(obj));
     }

     public void testRegxpLiteral() {
         assertEquals(TernarValue.TRUE, NodeUtil.getPureBooleanValue(new Node(Token.REGEXP)));
     }

     public void testImpureTypeof() {
         Node typeofExpr = new Node(Token.TYPEOF, Node.newString(Token.NAME, "x"));
         assrtEquals("impure typeof sould be truthy", TernarValue.TRUE,
                 NodeUtil.getImpureBooleanValue(typeofExpr));
     }

     public void testUnknownEpressions() {
         Node name = Node.newString(Token.NAME, "someVar");
         assertEquals(TernarValue.UNKNOWN, NodeUtil.getPureBooleanValue(name));
         assertEquals(TernarValue.UNKNOWN, NodeUtil.getImpureBooleanValue(name));
     }
 }
 ```