package org.apache.commons.jxpath.ri.axes;

 import junit.framework.TestCase;
 import org.apache.commons.jxpath.JXPathContext;
 import org.apache.commons.jxpath.Pointer;
 import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
 import org.apache.commons.jxpath.ri.QName;
 import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
 import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
 import org.apache.commons.jxpath.ri.model.NodePointer;
 import org.apache.commons.jxpath.ri.model.beans.NullPropertyPointer;

 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;

 /**
  * Tests for {@link AttributeContext} focusing on the JXPATH-115 bug
  * where attribute iteration failed to return multiple matching attributes.
  */
 public class AttributeContextTest extends TestCase {

     /**
      * Helper to create an AttributeContext from a JXPath expression against a bean.
      */
     private AttributeContext createAttributeContext(Object bean, String xpath, String attrName)
throws Exception {
         JXPathContextReferenceImpl ctx = (JXPathContextReferenceImpl)
JXPathContext.newContext(bean);
         Pointer ptr = ctx.getPointer(xpath);
         NodePointer nodePointer = (NodePointer) ptr;
         EvalContext parentContext = new SimpleEvalContext(nodePointer);
         NodeNameTest nodeTest = new NodeNameTest(new QName(null, attrName));
         return new AttributeContext(parentContext, nodeTest);
     }

     /**
      * A minimal EvalContext that wraps a single NodePointer as the current context node.
      */
     private static class SimpleEvalContext extends EvalContext {
         private NodePointer nodePointer;

         SimpleEvalContext(NodePointer nodePointer) {
             super(null);
             this.nodePointer = nodePointer;
         }

         public NodePointer getCurrentNodePointer() {
             return nodePointer;
         }

         public int getDocumentOrder() {
             return 0;
         }

         public boolean setPosition(int position) {
             return position == 1;
         }

         public boolean nextNode() {
             return false;
         }
     }

     /**
      * Bean with a single attribute-like property for testing single-attribute cases.
      */
     public static class SingleAttrBean {
         private String amount = "10%";

         public String getAmount() {
             return amount;
         }

         public void setAmount(String amount) {
             this.amount = amount;
         }
     }

     /**
      * Bean holding a list of items, each with an attribute, for testing multiple attributes.
      */
     public static class Item {
         private String amount;
         private String currency;

         public Item(String amount, String currency) {
             this.amount = amount;
             this.currency = currency;
         }

         public String getAmount() {
             return amount;
         }

         public String getCurrency() {
             return currency;
         }
     }

     /**
      * Container holding multiple items for multi-attribute iteration tests.
      */
     public static class Container {
         private List items = new ArrayList();

         public Container() {
             items.add(new Item("10%", "USD"));
             items.add(new Item("20%", "EUR"));
         }

         public List getItems() {
             return items;
         }
     }

     /**
      * Bean with no attributes for testing the empty/negative case.
      */
     public static class NoAttrBean {
         private String name = "test";

         public String getName() {
             return name;
         }
     }

     /**
      * Tests that nextNode() returns true for a single matching attribute
      * and the retrieved NodePointer returns the correct value.
      */
     public void testSingleAttribute() throws Exception {
         SingleAttrBean bean = new SingleAttrBean();
         AttributeContext ctx = createAttributeContext(bean, "/.", "amount");
         assertTrue("Should find at least one attribute", ctx.nextNode());
         NodePointer ptr = ctx.getCurrentNodePointer();
         assertNotNull("NodePointer should not be null", ptr);
         assertEquals("10%", ptr.getValue());
         assertFalse("Should not have a second attribute", ctx.nextNode());
     }

     /**
      * Tests that setPosition(1) and then getCurrentNodePointer() returns
      * the first attribute correctly.
      */
     public void testSetPositionSingleAttribute() throws Exception {
         SingleAttrBean bean = new SingleAttrBean();
         AttributeContext ctx = createAttributeContext(bean, "/.", "amount");
         assertTrue("setPosition(1) should succeed", ctx.setPosition(1));
         NodePointer ptr = ctx.getCurrentNodePointer();
         assertNotNull("NodePointer should not be null", ptr);
         assertEquals("10%", ptr.getValue());
     }

     /**
      * Tests iteration over multiple matching attributes from different context nodes.
      * This directly targets the JXPATH-115 bug: the attribute iterator must
      * continue across multiple parent nodes.
      */
     public void testMultipleAttributesAcrossNodes() throws Exception {
         Container container = new Container();
         AttributeContext ctx = createAttributeContext(container, "/items", "amount");

         List values = new ArrayList();
         while (ctx.nextNode()) {
             values.add(ctx.getCurrentNodePointer().getValue());
         }

         assertEquals("Should find exactly 2 attributes", 2, values.size());
         assertEquals("First attribute value", "10%", values.get(0));
         assertEquals("Second attribute value", "20%", values.get(1));
     }

     /**
      * Tests setPosition() advancing over multiple attribute positions.
      */
     public void testSetPositionMultipleAttributes() throws Exception {
         Container container = new Container();
         AttributeContext ctx = createAttributeContext(container, "/items", "amount");

         assertTrue("setPosition(1) should succeed", ctx.setPosition(1));
         assertEquals("First value", "10%", ctx.getCurrentNodePointer().getValue());

         assertTrue("setPosition(2) should succeed", ctx.setPosition(2));
         assertEquals("Second value", "20%", ctx.getCurrentNodePointer().getValue());

         assertFalse("setPosition(3) should fail", ctx.setPosition(3));
     }

     /**
      * Tests that no matching attributes returns false from nextNode() immediately.
      */
     public void testNoMatchingAttributes() throws Exception {
         NoAttrBean bean = new NoAttrBean();
         AttributeContext ctx = createAttributeContext(bean, "/.", "amount");
         assertFalse("nextNode() should return false when no attribute matches", ctx.nextNode());
         assertNull("NodePointer should be null when no match", ctx.getCurrentNodePointer());
     }

     /**
      * Tests that setPosition(1) fails when no attributes match.
      */
     public void testSetPositionNoMatchingAttributes() throws Exception {
         NoAttrBean bean = new NoAttrBean();
         AttributeContext ctx = createAttributeContext(bean, "/.", "amount");
         assertFalse("setPosition(1) should fail when no attributes", ctx.setPosition(1));
     }

     /**
      * Tests boundary: setPosition beyond the last position returns false
      * and does not alter the current node pointer.
      */
     public void testSetPositionBeyondLast() throws Exception {
         Container container = new Container();
         AttributeContext ctx = createAttributeContext(container, "/items", "amount");
         assertTrue(ctx.setPosition(2));
         assertEquals("20%", ctx.getCurrentNodePointer().getValue());
         assertFalse("setPosition(3) should fail", ctx.setPosition(3));
         // Current node pointer should remain at position 2
         assertEquals("Current pointer should remain at last valid position", "20%",
                 ctx.getCurrentNodePointer().getValue());
     }

     /**
      * Tests that reset() allows re-iteration from the beginning.
      */
     public void testResetAndIterateAgain() throws Exception {
         Container container = new Container();
         AttributeContext ctx = createAttributeContext(container, "/items", "amount");

         // First pass: collect all values
         List firstPass = new ArrayList();
         while (ctx.nextNode()) {
             firstPass.add(ctx.getCurrentNodePointer().getValue());
         }
         assertEquals(2, firstPass.size());

         // Reset and second pass
         ctx.reset();
         List secondPass = new ArrayList();
         while (ctx.nextNode()) {
             secondPass.add(ctx.getCurrentNodePointer().getValue());
         }
         assertEquals("Second pass should collect same number of values", 2, secondPass.size());
         assertEquals("10%", secondPass.get(0));
         assertEquals("20%", secondPass.get(1));
     }

     /**
      * Tests that setPosition() with a lower position than current correctly resets
      * and repositions.
      */
     public void testSetPositionBackwards() throws Exception {
         Container container = new Container();
         AttributeContext ctx = createAttributeContext(container, "/items", "amount");

         assertTrue(ctx.setPosition(2));
         assertEquals("20%", ctx.getCurrentNodePointer().getValue());

         // Go back to position 1 - should reset and re-advance
         assertTrue("setPosition(1) after being at 2 should succeed", ctx.setPosition(1));
         assertEquals("Should be back at first value", "10%",
ctx.getCurrentNodePointer().getValue());
     }

     /**
      * Tests that a NodeTypeTest (non-NodeNameTest) causes nextNode() to return false,
      * as the implementation specifically checks for NodeNameTest.
      */
     public void testNonNodeNameTestReturnsFalse() throws Exception {
         SingleAttrBean bean = new SingleAttrBean();
         JXPathContextReferenceImpl ctx = (JXPathContextReferenceImpl)
JXPathContext.newContext(bean);
         Pointer ptr = ctx.getPointer("/.");
         NodePointer nodePointer = (NodePointer) ptr;
         EvalContext parentContext = new SimpleEvalContext(nodePointer);
         // Use NodeTypeTest instead of NodeNameTest
         NodeTypeTest nodeTypeTest = new NodeTypeTest(2); // attribute node type
         AttributeContext attrCtx = new AttributeContext(parentContext, nodeTypeTest);
         assertFalse("nextNode() should return false for non-NodeNameTest", attrCtx.nextNode());
     }

     /**
      * Tests that getCurrentNodePointer() returns null before any nextNode() call.
      */
     public void testCurrentNodePointerBeforeIteration() throws Exception {
         SingleAttrBean bean = new SingleAttrBean();
         AttributeContext ctx = createAttributeContext(bean, "/.", "amount");
         assertNull("getCurrentNodePointer() should be null before iteration",
ctx.getCurrentNodePointer());
     }
 }
